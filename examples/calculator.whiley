// ====================================================
// A simple calculator for expressions
// ====================================================

define ADD as 0
define SUB as 1
define MUL as 2
define DIV as 3

// expression tree
define Expr as int |  // constant
    Var |              // variable
    BinOp |            // binary operator
    [Expr] |           // list constructor
    ListAccess         // list access
 
// binary operation
define BOp as { ADD, SUB, MUL, DIV }
define BinOp as { BOp op, Expr lhs, Expr rhs } 

// variables
define Var as { string id }

// list access
define ListAccess as { 
    Expr src, 
    Expr index
} 

// values
define Value as int | [Value]

// ====================================================
// Expression Evaluator
// ====================================================

null|Value evaluate(Expr e, {string->Value} env):
    if e is int:
        return e
    else if e is Var:
        return env[e.id]
    else if e is BinOp:
        lhs = evaluate(e.lhs, env)
        rhs = evaluate(e.rhs, env)
        // check if stuck
        if !(lhs is int && rhs is int):
            return null 
        // switch statement would be good
        if e.op == ADD:
            return lhs + rhs
        else if e.op == SUB:
            return lhs - rhs
        else if e.op == MUL:
            return lhs * rhs
        else if rhs != 0:
            return lhs / rhs
        return null // divide by zero
    else if e is [Expr]:
        r = []
        for i in e:
            v = evaluate(i, env)
            if v is null:
                return v // stuck
            else:
                r = r + [v]
        return r
    else:
        src = evaluate(e.src, env)
        index = evaluate(e.index, env)
        // santity checks
        if src is [Value] && index is int &&
            index >= 0 && index < |src|:
            return src[index]
        else:
            return null // stuck

// ====================================================
// Expression Parser
// ====================================================

define SyntaxError as { string err }
define State as { string input, int pos }

// Top-level parse method
Expr parse(string input) throws SyntaxError:
    init = {input: input, pos: 0}
    (e,st) = parseAddSubExpr(init)
    if st.pos != |input|:
        throw {err:"junk at end of input: " + st.input[st.pos..]}
    return e

(Expr, State) parseAddSubExpr(State st) throws SyntaxError:    
    // First, pass left-hand side
    lhs,st = parseMulDivExpr(st)
    
    st = parseWhiteSpace(st)
    // Second, see if there is a right-hand side
    if st.pos < |st.input| && st.input[st.pos] == '+':
        // add expression
        st.pos = st.pos + 1
        rhs,st = parseAddSubExpr(st)
        
        return {op: ADD, lhs: lhs, rhs: rhs},st
    else if st.pos < |st.input| && st.input[st.pos] == '-':
        // subtract expression
        st.pos = st.pos + 1
        (rhs,st) = parseAddSubExpr(st)
        
        return {op: SUB, lhs: lhs, rhs: rhs},st
    
    // No right-hand side
    return (lhs,st)

(Expr, State) parseMulDivExpr(State st) throws SyntaxError:    
    // First, pass left-hand side
    (lhs,st) = parseTerm(st)
    
    st = parseWhiteSpace(st)
    // Second, see if there is a right-hand side
    if st.pos < |st.input| && st.input[st.pos] == '*':
        // add expression
        st.pos = st.pos + 1
        (rhs,st) = parseMulDivExpr(st)        
        
        return {op: MUL, lhs: lhs, rhs: rhs}, st
    else if st.pos < |st.input| && st.input[st.pos] == '/':
        // subtract expression
        st.pos = st.pos + 1
        (rhs,st) = parseMulDivExpr(st)
        
        return {op: DIV, lhs: lhs, rhs: rhs}, st
    
    // No right-hand side
    return (lhs,st)

(Expr, State) parseTerm(State st) throws SyntaxError:
    st = parseWhiteSpace(st)    
    if st.pos < |st.input|:
        if isLetter(st.input[st.pos]):
            return parseIdentifier(st)
        else if isDigit(st.input[st.pos]):
            return parseNumber(st)
        else if st.input[st.pos] == '[':
            return parseList(st)
    throw ({err:"expecting number or variable"},st)

(Var, State) parseIdentifier(State st):    
    txt = ""
    // inch forward until end of identifier reached
    while st.pos < |st.input| && isLetter(st.input[st.pos]):
        txt = txt + st.input[st.pos]
        st.pos = st.pos + 1
    return ({id:txt}, st)

(Expr, State) parseNumber(State st):    
    n = 0
    // inch forward until end of identifier reached
    while st.pos < |st.input| && isDigit(st.input[st.pos]):
        n = n + st.input[st.pos] - '0'
        st.pos = st.pos + 1    
    return n, st

(Expr, State) parseList(State st) throws SyntaxError:    
    st.pos = st.pos + 1 // skip '['
    st = parseWhiteSpace(st)
    l = [] // initial list
    firstTime = true
    while st.pos < |st.input| && st.input[st.pos] != ']':
        if !firstTime && st.input[st.pos] != ',':
            throw {err: "expecting comma"}
        else if !firstTime:
            st.pos = st.pos + 1 // skip ','
        firstTime = false
        e,st = parseAddSubExpr(st)
        // perform annoying error check    
        l = l + [e]
        st = parseWhiteSpace(st)
    st.pos = st.pos + 1
    return l,st
 
// Parse all whitespace upto end-of-file
State parseWhiteSpace(State st):
    while st.pos < |st.input| && isWhiteSpace(st.input[st.pos]):
        st.pos = st.pos + 1
    return st

// Determine what is whitespace
bool isWhiteSpace(char c):
    return c == ' ' || c == '\t' || c == '\n'    

// ====================================================
// Main Method
// ====================================================

public void System::main([string] args):
    env = {"x"->1,"y"->2}
    if(|args| > 0):
        e = parse(args[0])
        result = evaluate(e,env)
        out.println(str(result))
    else:
        out.println("no parameter provided!")
