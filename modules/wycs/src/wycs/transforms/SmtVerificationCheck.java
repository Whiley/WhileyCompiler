// Copyright (c) 2014, Henry J. Wylde (hjwylde@gmail.com)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wycs.transforms;

import static wycc.lang.SyntaxError.internalFailure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import wybs.lang.Builder;
import wycc.lang.Transform;
import wycc.util.Pair;
import wycs.builders.Wyal2WycsBuilder;
import wycs.core.Code;
import wycs.core.SemanticType;
import wycs.core.Value;
import wycs.core.WycsFile;
import wycs.solver.smt.Block;
import wycs.solver.smt.Logic;
import wycs.solver.smt.Option;
import wycs.solver.smt.Response;
import wycs.solver.smt.Smt2File;
import wycs.solver.smt.Sort;
import wycs.solver.smt.Stmt;

/**
 * A SMT verification checker. This class will first re-write the {@link wycs.core.WycsFile} into a
 * {@link wycs.solver.smt.Smt2File}. Then it will attempt to run an external SMT solver on it to
 * prove its correctness. If it cannot be proved correct, then an error is thrown.
 *
 * @author Henry J. Wylde
 */
public final class SmtVerificationCheck implements Transform<WycsFile> {

    private static final long SMT2_TIMEOUT = 10;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

    private static final String VAR_PREFIX = "r";
    private static final String GEN_VAR_PREFIX = "g";

    private final Wyal2WycsBuilder builder;

    /**
     * Determines whether this transform is enabled or not.
     */
    private boolean enabled = getEnable();
    /**
     * Determines whether debugging is enabled or not.
     */
    private boolean debug = getDebug();
    /**
     * The external SMT solver to use for verification.
     */
    private String solver = getSolver();

    /**
     * The WycsFile we are currently applying this check to.
     */
    private WycsFile wycsFile;

    /**
     * The SMT2 file we are generating during the verification check process.
     */
    private Smt2File smt2File;
    /**
     * The current assertion block we are building.
     */
    private Block block;
    /**
     * The current extra conditions we are building. When we go into an assertion, this stack has a
     * new list pushed onto it. If any of the expressions used inside the quantifier require an
     * extra condition to be generated (e.g., for constant generation), then they may add a
     * condition into the current list and the surrounding assertion will add it in as a condition
     * before it generates itself.
     */
    private Stack<List<String>> conditions;
    /**
     * A list of assertions in the order they are written. Allows us to still use the error messages
     * by matching them to their corresponding "check-sat" statement when we verify the SMT file.
     * Each assertion is paired to an expected result, one of either {@value
     * wycs.solver.smt.Response#SAT} or {@value wycs.solver.smt.Response#UNSAT}. When verifying the
     * file, if the assertion does not match the expected result then an error is thrown.
     */
    private List<Pair<WycsFile.Assert, String>> assertions;
    /**
     * A unique generator for variable / constant names.
     */
    private int gen = 0;
    /**
     * A list of uninterpreted functions that have already had their declaration and assertion
     * statements added into the current block. This list should be cleared each time a new block is
     * created. This list is used to help prevent a {@link java.lang.StackOverflowError} in the
     * event a function is recursive.
     */
    private Set<Pair<String, Map<String, SemanticType>>> functions;

    /**
     * Creates a new {@code SmtVerificationCheck} with the given project builder.
     *
     * @param builder the builder.
     */
    public SmtVerificationCheck(Builder builder) {
        this.builder = (Wyal2WycsBuilder) builder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(WycsFile file) throws IOException {
        if (!enabled) {
            return;
        }

        // Reset the helper variables
        wycsFile = file;
        smt2File = new Smt2File();
        assertions = new ArrayList<Pair<WycsFile.Assert, String>>();
        conditions = new Stack();
        gen = 0;
        functions = new HashSet<Pair<String, Map<String, SemanticType>>>();

        // Write out the header
        writeHeader();

        // Translate the WycsFile
        for (WycsFile.Declaration declaration : file.declarations()) {
            translate(declaration);
        }

        // Write out the footer
        writeFooter();

        // Attempt to verify the generated SMT2 file
        verify(write());
    }

    /**
     * Gets the description of the debug option.
     *
     * @return the debug description.
     */
    public static String describeDebug() {
        return "Enable/disable debugging information";
    }

    /**
     * Gets the description of the enable option.
     *
     * @return the enable description.
     */
    public static String describeEnable() {
        return "Enable/disable verification";
    }

    /**
     * Gets the description of the solver option.
     *
     * @return the solver description.
     */
    public static String describeSolver() {
        return "Set the external SMT solver to use";
    }

    /**
     * Gets the default value of the debug option. The default is {@value false}.
     *
     * @return the debug default value.
     */
    public static boolean getDebug() {
        return false;
    }

    /**
     * Gets the default value of the enable option. The default is {@value true}.
     *
     * @return the enable default value.
     */
    public static boolean getEnable() {
        return false;
    }

    /**
     * Gets the default value of the solver option. The default is {@link
     * wycs.solver.smt.Solver#Z3}.
     *
     * @return the solver default value.
     */
    public static String getSolver() {
    	return System.getenv("SMT_SOLVER");
    }

    /**
     * Sets the value of the debug option.
     *
     * @param flag the new debug value.
     */
    public void setDebug(boolean flag) {
        this.debug = flag;
    }

    /**
     * Sets the value of the enable option.
     *
     * @param flag the new enable value.
     */
    public void setEnable(boolean flag) {
        this.enabled = flag;
    }

    /**
     * Sets the value of the solver option. The value should be one of the available enumeration
     * values from {@link wycs.solver.smt.Solver}.
     *
     * @param solver the new solver value.
     */
    public void setSolver(String solver) {
        this.solver = solver;
    }

    /**
     * Builds up a generic binding map that can be used to instantiate generics in function or macro
     * definitions.
     *
     * @param from the from array of variable types to get the names from.
     * @param to the to array of types.
     * @return the generic binding map.
     */
    private Map<String, SemanticType> buildGenericBinding(SemanticType[] from, SemanticType[] to) {
        Map<String, SemanticType> binding = new HashMap();
        for (int i = 0; i < to.length; i++) {
            SemanticType.Var var = (SemanticType.Var) from[i];

            binding.put(var.name(), to[i]);
        }

        return binding;
    }

    /**
     * Generates a unique name for a new variable.
     *
     * @return a new unique variable name.
     */
    private String generateVariable() {
        return GEN_VAR_PREFIX + gen++;
    }

    private Sort getSort(SemanticType type) {
        if (type instanceof SemanticType.And) {
            // TODO: Implement type instanceof SemanticType.And
        } else if (type instanceof SemanticType.Any) {
            return Sort.ANY;
        } else if (type instanceof SemanticType.Bool) {
            return Sort.BOOL;
        } else if (type instanceof SemanticType.Function) {
            // TODO: Implement type instanceof SemanticType.Function
        } else if (type instanceof SemanticType.Int) {
            return Sort.INT;
        } else if (type instanceof SemanticType.Not) {
            // TODO: Implement type instanceof SemanticType.Not
        } else if (type instanceof SemanticType.Or) {
            // TODO: Implement type instanceof SemanticType.Or
        } else if (type instanceof SemanticType.Real) {
            return Sort.REAL;
        } else if (type instanceof SemanticType.Array) {
            SemanticType.Array set = (SemanticType.Array) type;

            String inner;
            // An empty set has an inner type of Void, which we can't actually translate properly
            if (set.element() instanceof SemanticType.Void) {
                // If it's an empty set, treat it as a set of Ints, that way we can still generate
                // the set functions (i.e., empty(), length(set), etc.)
                inner = Sort.INT.toString();
            } else {
                inner = translate(set.element());
            }

            return new Sort.Set(inner);
        } else if (type instanceof SemanticType.Tuple) {
            SemanticType.Tuple tuple = (SemanticType.Tuple) type;

            List<String> inners = new ArrayList();
            for (int i = 0; i < tuple.size(); i++) {
                inners.add(translate(tuple.tupleElement(i)));
            }

            return new Sort.Tuple(inners);
        } else if (type instanceof SemanticType.Var) {
            // TODO: Implement type instanceof SemanticType.Var
        } else if (type instanceof SemanticType.Void) {
            // TODO: Implement type instanceof SemanticType.Void
        }

        // Can't use internalFailure as Value isn't a syntactic element
        throw new InternalError("getSort(SemanticType) not fully implemented: " + type.getClass());
    }

    /**
     * Reads in an input stream and converts it to a {@link java.lang.String}. Uses the {@link
     * java.nio.charset.StandardCharsets#UTF_8} character set by default.
     *
     * @param in the input stream to read fully.
     * @return the string.
     * @throws IOException if the stream could not be read.
     */
    private static String readInputStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        try {
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            return sb.toString();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    /**
     * Simplifies the given load code. This method first recursively simplifies the operand of the
     * load before attempting to simplify the load itself. The load may only be simplified if it has
     * an opcode of {@link Code.Op#TUPLE}, wherein the simplification is performed by looking up the
     * correct element based on the load's index field. The result of this is a complete
     * simplification of loads over known tuples.
     *
     * @param code the tuple load code to simplify.
     * @return the (if possible) simplified code.
     */
    private Code simplify(Code.Load code) {
        // Recurse into the operand first
        Code operand = simplify(code.operands[0]);

        // Simplify known tuples
        if (operand.opcode == Code.Op.TUPLE) {
            operand = operand.operands[code.index];
        }
        // We can't simplify Code.Op.CONST because that returns a Value, which doesn't subtype Code

        return simplify(operand);
    }

    /**
     * Attempts to simplify the given code. Currently the simplification will only work if the code
     * is a {@link wycs.core.Code.Load}.
     *
     * @param code the code to simplify.
     * @return the (if possible) simplified code.
     */
    private Code simplify(Code code) {
        if (code instanceof Code.Load) {
            return simplify((Code.Load) code);
        }

        return code;
    }

    private void translate(WycsFile.Declaration declaration) {
        if (declaration instanceof WycsFile.Assert) {
            translate((WycsFile.Assert) declaration);
        } else if (declaration instanceof WycsFile.Function) {
            translate((WycsFile.Function) declaration);
        } else if (declaration instanceof WycsFile.Macro) {
            translate((WycsFile.Macro) declaration);
        } else {
            internalFailure("translate(WycsFile.Declaration) not fully implemented: " + declaration
                    .getClass(), wycsFile.filename(), declaration);
        }
    }

    private void translate(WycsFile.Assert declaration) {
        // Use a new block for each assertion to remove redundant declarations after each
        // "(check-sat)"
        // Should help to prevent exponential bloating
        // Each block automatically outputs a "(push 1)" and "(pop 1)" statement when being written
        // out
        block = new Block();

        // Clear the list of defined functions
        functions.clear();

        // Push an extra conditions list onto the stack
        conditions.add(new ArrayList<String>());

        // Reset the variable name counter
        gen = 0;

        // Returns a pair, (expr, result)
        Pair<String, String> pair = translateAssertCode(declaration.condition);

        // Add the declaration and expected result to assertions, this will be used when we verify
        // the translated file
        assertions.add(new Pair(declaration, pair.second()));

        // Add the extra conditions and then the code
        // The extra conditions here aren't surrounded by a quantifier, so we must add them each as
        // assertions
        List<String> extras = conditions.pop();
        for (String extra : extras) {
            block.append(new Stmt.Assert(extra));
        }

        block.append(new Stmt.Assert(pair.first()));
        block.append(new Stmt.CheckSat());

        smt2File.append(block);

        // Sanity assignment to ensure code isn't called without a block!
        block = null;
    }

    private String translate(Code<?> code) {
        switch (code.opcode) {
            case VAR:
                return translate((Code.Variable) code);
            case CONST:
                return translate((Code.Constant) code);
            case EXISTS:
            case FORALL:
                return translate((Code.Quantifier) code);
            case FUNCALL:
                return translate((Code.FunCall) code);
            case LOAD:
                return translate((Code.Load) code);
            case LENGTH:
            case NEG:
            case NOT:
            case NULL:
                return translate((Code.Unary) code);
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case REM:
            case EQ:
            case NEQ:
            case LT:
            case LTEQ:
                return translate((Code.Binary) code);
            case AND:
            case OR:
            case TUPLE:
            case ARRAY:
                return translate((Code.Nary) code);
            default:
                internalFailure("translate(Code<?>) not fully implemented: " + code.opcode,
                        wycsFile.filename(), code);
        }

        throw new InternalError("DEADCODE");
    }

    private String translate(Code.Unary code) {
        String target = translate(code.operands[0]);
        String op = null;

        switch (code.opcode) {
            case LENGTH:
                op = "length";
                break;
            case NEG:
                op = "-";
                break;
            case NOT:
                op = "not";
                break;
            case NULL:
                // TODO: Implement code.opcode == NULL
            default:
                internalFailure("translate(Code.Unary) not fully implemented: " + code.opcode,
                        wycsFile.filename(), code);
        }

        return "(" + op + " " + target + ")";
    }

    private String translate(Code.Nary code) {
        String op = null;

        switch (code.opcode) {
            case AND:
                op = "and";
                break;
            case OR:
                op = "or";
                break;
            case TUPLE:
                return translateTuple(code);
            case ARRAY:
                return translateSet(code);
            default:
                internalFailure("translate(Code.Nary) not fully implemented: " + code.opcode,
                        wycsFile.filename(), code);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(op);
        sb.append(" ");
        for (int i = 0; i < code.operands.length; i++) {
            sb.append(translate(code.operands[i]));

            if (i < code.operands.length - 1) {
                sb.append(" ");
            }
        }
        sb.append(")");

        return sb.toString();
    }

    private String translate(Code.Binary code) {
        String lhs = translate(code.operands[0]);
        String rhs = translate(code.operands[1]);
        String op = null;

        switch (code.opcode) {
            case ADD:
                op = "+";
                break;
            case SUB:
                op = "-";
                break;
            case MUL:
                op = "*";
                break;
            case DIV:
                op = "div";
                break;
            case REM:
                op = "rem";
                break;
            case EQ:
                op = "=";
                break;
            case NEQ:
                op = "distinct";
                break;
            case LT:
                op = "<";
                break;
            case LTEQ:
                op = "<=";
                break;
            default:
                internalFailure("translate(Code.Binary) not fully implemented: " + code.opcode,
                        wycsFile.filename(), code);
        }

        return "(" + op + " " + lhs + " " + rhs + ")";
    }

    private String translate(Code.Quantifier code) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");

        switch (code.opcode) {
            case EXISTS:
                sb.append("exists");
                break;
            case FORALL:
                sb.append("forall");
                break;
            default:
                internalFailure("translate(Code.Quantifier) not fully implemented: " + code.opcode,
                        wycsFile.filename(), code);
        }

        // Add the parameter declarations
        sb.append(" (");
        for (int i = 0; i < code.types.length; i++) {
            Pair<SemanticType, Integer> pair = code.types[i];

            sb.append("(");
            sb.append(VAR_PREFIX);
            sb.append(pair.second());
            sb.append(" ");
            sb.append(translate(pair.first()));
            sb.append(")");

            if (i < code.types.length - 1) {
                sb.append(" ");
            }
        }
        sb.append(") ");

        // An EXISTS can't use the conditions push feature, only a FORALL can, so let's bypass it
        // If we let an EXISTS add in the extra conditions that are added as an implication, it
        // would always return true as it could just turn the conditions false and the whole
        // quantifier expression would then be true
        if (code.opcode == Code.Op.EXISTS) {
            return sb.append(translate(code.operands[0])).append(")").toString();
        }

        // Push an extra conditions list onto the stack
        conditions.push(new ArrayList<String>());

        // Translate the code
        String expr = translate(code.operands[0]);

        // Add the extra conditions and then the code
        // The extra conditions are added in an implication way, i.e., if a and b are extra
        // conditions and c is the original expression, we add (a and b implies c), or
        // (or (not a) (not b) c)

        List<String> extras = conditions.pop();
        if (extras.isEmpty()) {
            return sb.append(expr).append(")").toString();
        }

        sb.append("(or ");
        for (String extra : extras) {
            sb.append("(not ");
            sb.append(extra);
            sb.append(") ");
        }

        sb.append(expr);

        sb.append("))");

        return sb.toString();
    }

    private String translate(Code.FunCall code) {
        // Working with uninterpreted functions now
        // We want to make an uninterpreted function declaration and an assertion over the function
        // i.e. abs(int x) => int y: x >= 0 ==> y > 0
        // Has the declaration: abs(int) => int
        // and the assertion: forall (r1 int) . r1 >= 0 ==> abs(r1) > 0
        // (note: functions have the return value numbered as 0, so the parameters start from 1)

        WycsFile module = null;
        try {
            module = builder.getModule(code.nid.module());
        } catch (Exception e) {
            internalFailure(e.getMessage(), wycsFile.filename(), code, e);
        }

        if (module == null) {
            throw new InternalError("module '" + code.nid.module() + "' not found");
        }

        WycsFile.Declaration decl = module.declaration(code.nid.name());

        if (decl == null) {
            throw new InternalError(
                    "function call to a non-declared function (name=" + code.nid.name() + ")");
        }
        if (!(decl instanceof WycsFile.Function)) {
            internalFailure("function call to a non-function declaration", wycsFile.filename(),
                    decl);
        }

        String id = code.nid.module() + "_" + code.nid.name();

        // Get the function declaration and instantiate its generic types
        WycsFile.Function function = (WycsFile.Function) decl;
        Map<String, SemanticType> generics = buildGenericBinding(function.type.generics(),
                code.type.generics());
        SemanticType.Function type = (SemanticType.Function) function.type.substitute(generics);

        // Recursion potential here! Skip if we've already generated the declaration and assertion

        // A function can be uniquely identified by it's identifier and generic binding
        if (!functions.contains(new Pair<String, Map<String, SemanticType>>(id, generics))) {
            functions.add(new Pair<String, Map<String, SemanticType>>(id, generics));

            // Generate the uninterpreted function declaration

            List<String> parameters = new ArrayList<String>();
            parameters.add(translate(type.from()));
            String returnSort = translate(type.to());

            block.append(new Stmt.DeclareFun(id, parameters, returnSort));

            // Generate the uninterpreted function assertion
            if (function.constraint != null) {
                // Create a binding that replaces the named return variable with the function call
                Map<Integer, Code> binding = new HashMap();
                binding.put(0, Code.FunCall(type, Code.Variable(type.from(), 1), code.nid, code.binding));

                // Substitute and instantiate to get the new operand for the assertion
                Code operand = function.constraint.substitute(binding).instantiate(generics);
                Pair<SemanticType, Integer>[] types = new Pair[] {new Pair(type.from(), 1)};
                // TODO: What type should a quantifier have?
                Code assertion = Code.Quantifier(SemanticType.Bool, Code.Op.FORALL, operand, types);

                block.append(new Stmt.Assert(translate(assertion)));
            }
        }

        return "(" + id + " " + translate(code.operands[0]) + ")";
    }

    private String translate(Code.Load code) {
        Code operand = simplify(code.operands[0]);

        // Check to see if we can simplify this expression
        if (operand.opcode == Code.Op.CONST) {
            Value.Tuple tuple = (Value.Tuple) ((Code.Constant) operand).value;

            return translate(tuple.values.get(code.index));
        } else if (operand.opcode == Code.Op.TUPLE) {
            return translate(operand.operands[code.index]);
        }

        // Guess not, let's just return the translated load

        return "(" + Sort.Tuple.generateGetFunctionName(code.index) + " " + translate(
                code.operands[0]) + ")";
    }

    private String translate(Code.Constant code) {
        return translate(code.value);
    }

    private String translate(Code.Variable code) {
        return VAR_PREFIX + code.index;
    }

    private void translate(WycsFile.Function declaration) {
        // Ignore, functions can be generic, so we must generate the code for them at the time of
        // the function call
    }

    private void translate(WycsFile.Macro declaration) {
        // Ignore, macro expansion should have handled this
    }

    private String translate(Value value) {
        if (value instanceof Value.Bool) {
            return ((Value.Bool) value).value ? "true" : "false";
        } else if (value instanceof Value.Decimal) {
            return ((Value.Decimal) value).value.toString();
        } else if (value instanceof Value.Integer) {
            return ((Value.Integer) value).value.toString();
        } else if (value instanceof Value.Array) {
            return translate((Value.Array) value);
        } else if (value instanceof Value.Tuple) {
            return translate((Value.Tuple) value);
        }

        // Can't use internalFailure as Value isn't a syntactic element
        throw new InternalError("translate(Value) not fully implemented: " + value.getClass());
    }

    private String translate(Value.Array value) {
        // Trigger the addition of the set functions
        translate(value.type());

        Sort.Set sort = (Sort.Set) getSort(value.type());

        // Create the in-lined constant expression
        String expr = sort.getEmptyNameAsQualified();
        for (Value inner : value.values) {
            expr = "(" + Sort.Set.FUN_ADD_NAME + " " + expr + " " + translate(inner) + ")";
        }

        return expr;
    }

    private String translate(Value.Tuple value) {
        // Trigger the addition of the tuple functions
        translate(value.type());

        String var = generateVariable();

        // Add the constant declaration
        block.append(new Stmt.DeclareFun(var, Collections.EMPTY_LIST, translate(value.type())));

        // Create the extra conditions to assert the value of the tuple
        for (int i = 0; i < value.values.size(); i++) {
            String extra = "(= (" + Sort.Tuple.generateGetFunctionName(i) + " " + var + ") "
                    + translate(value.values.get(i)) + ")";

            conditions.peek().add(extra);
        }

        return var;
    }

    private String translate(SemanticType type) {
        Sort sort = getSort(type);

        // Generate some initialisation statements for the sort and relevant functions
        block.append(sort.generateInitialisers());

        return sort.toString();
    }

    /**
     * Translates the given code, knowing that it will be used in a {@link
     * wycs.solver.smt.Stmt.Assert}. This method will attempt to optimise the translation if the
     * code is a quantifier.
     * <p/>
     * An EXISTS optimisation is performed by skolemization and simply asserting the condition with
     * this new generated variable.
     * <p/>
     * A FORALL optimisation is performed by splitting up the operand and adding the premises and
     * negated goal as assertions. If this optimisation is performed correctly, the verifier should
     * check for an {@value wycs.solver.smt.Response#UNSAT} instead of a {@value
     * wycs.solver.smt.Response#SAT}. This expectation is sent back to the caller in the return
     * value.
     * <p/>
     * Returns a pair of strings. The first element is the final expression to add as an assertion,
     * the second element is the expected result, one of {@value wycs.solver.smt.Response#SAT} or
     * {@value wycs.solver.smt.Response#UNSAT}.
     *
     * @param code the assertion code to translate.
     * @return the expression and expected result as a pair.
     */
    private Pair<String, String> translateAssertCode(Code<?> code) {
        // Can we do the optimisation?
        if (!(code instanceof Code.Quantifier)) {
            // Guess not, let's negate it and check for UNSAT anyway as UNSAT is easier for a solver
            // to determine (and lets our length function work properly)
            return new Pair<String, String>("(not " + translate(code) + ")", Response.UNSAT);
        }

        Code.Quantifier quantifier = (Code.Quantifier) code;
        Code<?> operand = code.operands[0];

        // Let's introduce some constants for the quantifier variables

        for (int i = 0; i < quantifier.types.length; i++) {
            Pair<SemanticType, Integer> pair = quantifier.types[i];

            block.append(new Stmt.DeclareFun(VAR_PREFIX + pair.second(), Collections.EMPTY_LIST,
                    translate(pair.first())));
        }

        // Check if the opcode is an EXISTS, if it is, then we translate it as follows:
        // exists x : a and b and c
        // Translates to:
        // declare-sort x
        // assert a and b and c
        if (code.opcode == Code.Op.EXISTS) {
            // Potential limitation here, if we're checking for SAT, then our length function may
            // not work properly. Ideally, we'd check for UNSAT but I don't know how to do that in
            // an EXISTS. Overall, for a Whiley file it shouldn't matter as they're all translated
            // to a FORALL, but the limitation still exists for other potential uses of WyAL and
            // WyCS
            return new Pair<String, String>(translate(operand), Response.SAT);
        }

        // Opcode must be a FORALL

        // We treat the operand as if it is an OR, as that is how an implication is translated
        // (i.e., a and b => c === !a or !b or c)

        // We translate it as follows:
        // forall x : !a or !b or c
        // Translates to:
        // declare-sort x
        // assert a
        // assert b
        // assert !c

        // If it's unsatisfiable then we know it's true

        // If it doesn't happen to be an OR, then we can still translate it treating it as if it
        // were an or of a single term
        if (operand.opcode != Code.Op.OR) {
            return new Pair<String, String>("(not " + translate(operand) + ")", Response.UNSAT);
        }

        // Looks like the FORALL is over an OR (or implication), got to add the negated premises

        for (int i = 0; i < operand.operands.length - 1; i++) {
            // The implication will have already negated the arguments, we need to change them back
            block.append(new Stmt.Assert("(not " + translate(operand.operands[i]) + ")"));
        }

        // Translate the final expression and negate it
        String expr = translate(operand.operands[operand.operands.length - 1]);
        expr = "(not " + expr + ")";

        return new Pair<String, String>(expr, Response.UNSAT);
    }

    private String translateSet(Code.Nary code) {
        // Trigger the addition of the set functions
        translate(code.returnType());

        Sort.Set sort = (Sort.Set) getSort(code.returnType());

        // Create the in-lined constant expression
        String expr = sort.getEmptyNameAsQualified();
        for (Code inner : code.operands) {
            expr = "(" + Sort.Set.FUN_ADD_NAME + " " + expr + " " + translate(inner) + ")";
        }

        return expr;
    }

    private String translateTuple(Code.Nary code) {
        // Trigger the addition of the tuple functions
        translate(code.returnType());

        String var = generateVariable();

        // Add the constant declaration
        block.append(new Stmt.DeclareFun(var, Collections.EMPTY_LIST, translate(
                code.returnType())));

        // Create the extra conditions to assert the value of the tuple
        for (int i = 0; i < code.operands.length; i++) {
            String extra = "(= (" + Sort.Tuple.generateGetFunctionName(i) + " " + var + ") "
                    + translate(code.operands[i]) + ")";

            conditions.peek().add(extra);
        }

        return var;
    }

    /**
     * Runs a solver on the given file and checks that all of the assertions passed. If any
     * assertion failed, then an appropriate error is thrown.
     *
     * @param file the file to verify.
     * @throws IOException if the file could not be read.
     */
    private void verify(File file) throws IOException {
        // Create the process to call the solver
        List<String> args = new ArrayList();
        String solver = getSolver();
        if(solver == null) {
        	throw new InternalError("Environment variable $SMT_SOLVER not set");
        }
        args.add(getSolver());
        // Add the solvers custom arguments
        args.add(file.getAbsolutePath());
        ProcessBuilder pb = new ProcessBuilder(args);
        final Process process = pb.start();

        List<String> output = null;
        Timer timer = new Timer();
        try {
            // TODO: Fix this up and don't use a timer, two exceptions are being thrown with this implementation
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    process.destroy();
                    throw new SolverFailure(
                            "solver timed out after " + SMT2_TIMEOUT + " " + TIMEOUT_UNIT
                                    .toString());
                }
            }, TimeUnit.MILLISECONDS.convert(SMT2_TIMEOUT, TIMEOUT_UNIT));

            process.waitFor();
            timer.cancel();

            if (process.exitValue() != 0) {
                throw new SolverFailure(
                        "verification exited with non-zero value: " + readInputStream(
                                process.getInputStream()));
            }

            // Read all of the standard output from the process
            output = Arrays.asList(readInputStream(process.getInputStream()).split("\n"));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // Make sure the process is finished
            process.destroy();
        }

        if (output == null) {
            throw new RuntimeException("verification did not complete");
        }

        // A counter for what assertion we're up to
        // Each "(check-sat)" command in the file corresponds to an assertion in the #assertions
        // field
        int index = 0;
        for (String line : output) {
            if (line.isEmpty()) {
                continue;
            }

            // Get the current assertion and expected result
            Pair<WycsFile.Assert, String> pair = assertions.get(index);
            WycsFile.Assert assertion = pair.first();
            String expectedResult = pair.second();

            if (line.equals(expectedResult)) {
                // Assertion was valid, move to the next assertion
                index++;
            } else if (line.equals(Response.UNKNOWN)) {
                throw new SolverFailure("solver returned unknown");
            } else if (line.equals(Response.SAT) || line.equals(Response.UNSAT)) {
                // Assertion was invalid, create an appropriate error
                if (assertion.message == null) {
                    throw new AssertionFailure(assertion);
                } else {
                    throw new AssertionFailure(assertion.message, assertion);
                }
            } else if (line.equals(Response.UNSUPPORTED)) {
                // A set-option was unsupported, skip this line
            } else if (line.startsWith("(error")) {
                // Internal error occurred that shouldn't have, unwrap the error message
                String error = line.substring(7, line.length() - 1);
                throw new SolverFailure(error);
            } else {
                throw new RuntimeException(line);
            }
        }
    }

    /**
     * Writes out the {@link #smt2File} to a temporary file and returns it. This method also adds a
     * trap onto the file to ensure it is deleted upon application exit.
     *
     * @return the written temporary file.
     * @throws IOException if the file could not be written.
     */
    private File write() throws IOException {
        // Prepare the output destination
    	// FIXME: the following is a bit of a hack and needs to be fixed!
        File out = new File(wycsFile.filename().replace(".whiley",".smt2").replace(".wyal",".smt2"));
        out.deleteOnExit();

        FileOutputStream fos = null;
        try {
            // Create the stream and write out the contents to it
            fos = new FileOutputStream(out);
            fos.write(smt2File.toString().getBytes("UTF-8"));
            fos.flush();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        return out;
    }

    /**
     * Writes out the footer to the {@link #smt2File}.
     */
    private void writeFooter() {
        smt2File.append(new Stmt.Exit());
    }

    /**
     * Writes out the header to the {@link #smt2File}. The header includes options to set and the
     * logic to use.
     */
    private void writeHeader() {
        // Don't print "success" for each command
        smt2File.append(new Stmt.SetOption(Option.PRINT_SUCCESS, "false"));

        // Append the logic
        smt2File.append(new Stmt.SetLogic(Logic.AUFNIRA));
    }

    /**
     * Represents a failure that occurred in an assertion, i.e., an assertion was found to be either
     * {@value wycs.solver.smt.Response#UNSAT} or {@value wycs.solver.smt.Response#UNKNOWN}.
     * Appropriate information is provided to give a detailed error message to the user about the
     * particular assertion that failed.
     *
     * @author Henry J. Wylde
     */
    public static class AssertionFailure extends RuntimeException {

        private WycsFile.Assert assertion;

        /**
         * Creates a new {@code AssertionFailure} with the given error message and assertion.
         *
         * @param message the error message.
         * @param assertion the failed assertion.
         */
        public AssertionFailure(String message, WycsFile.Assert assertion) {
            super(message);

            if (assertion == null) {
                throw new NullPointerException("assertion cannot be null");
            }

            this.assertion = assertion;
        }

        /**
         * Creates a new {@code AssertionFailure} with the given assertion.
         *
         * @param assertion the failed assertion.
         */
        public AssertionFailure(WycsFile.Assert assertion) {
            this("assertion failure", assertion);
        }

        /**
         * Gets the assertion that caused this failure.
         *
         * @return the assertion.
         */
        public WycsFile.Assert getAssertion() {
            return assertion;
        }
    }

    /**
     * Represents a failure that occurred in the SMT solver. Failures in the SMT solver are {@link
     * java.lang.InternalError}s as they should never occur.
     *
     * @author Henry J. Wylde
     */
    public static class SolverFailure extends InternalError {

        /**
         * Creates a new {@code SolverFailure} with the given error message.
         *
         * @param message the error message.
         */
        public SolverFailure(String message) {
            super(message);
        }
    }
}
