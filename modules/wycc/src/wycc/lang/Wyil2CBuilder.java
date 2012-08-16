// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
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

package wycc.lang;

import java.io.*;
import java.util.*;

import wybs.lang.Builder;
import wybs.lang.Logger;
import wybs.lang.NameSpace;
import wybs.lang.Path;
import wyil.lang.WyilFile;
import wyil.util.Pair;

import wybs.lang.Path;
import wyil.lang.Attribute;
import wyil.lang.Block;
import wyil.lang.Code;
import wyil.lang.Code.Const;
import wyil.lang.Code.BinStringKind;
import wyil.lang.Constant.Bool;
import wyil.lang.Modifier;
import wyil.lang.NameID;
import wyil.lang.Type;
import wyil.lang.Constant;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Case;
import wyil.lang.WyilFile.ConstantDeclaration;
import wyil.lang.WyilFile.MethodDeclaration;
import wyil.lang.WyilFile.TypeDeclaration;

public class Wyil2CBuilder implements Builder {
	private Logger logger = Logger.NULL;
	// private final PrintStream output = null;
	private final String defaultManglePrefix = "wycc__";
	private final String includeFile = "#include \"wycc_lib.h\"\n";
	private String optIncludeFile = "";
	private String manglePrefix = null;
	private int initor_flg = 1;
	private String name;
	private boolean debugFlag;
	private boolean lineNumFlag;
	private final int wyccTypeAny = 0;
	private final int wyccTypeNone = -1;
	private final String exit_fail = "exit(-4);";
	
	public Wyil2CBuilder() {
		this.debugFlag = true;
		this.lineNumFlag = true;
	}
	
	public Wyil2CBuilder(boolean dflg) {
		this.debugFlag = dflg;
		this.lineNumFlag = true;
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public NameSpace namespace() {
		return null; // TODO: this seems like a mistake in Builder ?
	}

	private void addIncludeFail(){
		this.optIncludeFile += "#include <stdlib.h>\n";
		this.optIncludeFile += "#include <stdio.h>\n";
	}
	
	public void build(List<Pair<Path.Entry<?>,Path.Entry<?>>> delta) throws IOException {
		
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();
	
		// ========================================================================
		// Translate files
		// ========================================================================

		for(Pair<Path.Entry<?>,Path.Entry<?>> p : delta) {
			Path.Entry<?> f = p.second();
			if(f.contentType() == CFile.ContentType) {
				Path.Entry<WyilFile> sf = (Path.Entry<WyilFile>) p.first();
				Path.Entry<CFile> df = (Path.Entry<CFile>) f;
				// build the C-File
				CFile contents = build(sf.read());								
				// finally, write the file into its destination
				df.write(contents);
			}
		}

		// ========================================================================
		// Done
		// ========================================================================

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Wyil => C: compiled " + delta.size()
				+ " file(s)", endTime - start, memory - runtime.freeMemory());
	}	
	

	public void setManglePrefix(String str) {
		this.manglePrefix = str;
	}

	public String getManglePrefix() {
		if (this.manglePrefix == null) {
			this.manglePrefix = this.defaultManglePrefix;
		}
		return this.manglePrefix;
	}
	
	/*
	 * convert contents of a wyil file to contents of a C file
	 */
	protected CFile build(WyilFile module) {		
		//String contents = "// WYIL MODULE: " + module.id();
		String contents = "";
		String tmp;
		int cnt;
		List<Method> mets = new ArrayList<Method>();
		//System.err.println("Got to my code.");
		this.name = module.id().toString();
		contents += this.writePreamble(module);
		//System.err.println("milestone 1.");
		Collection<TypeDeclaration> typCol = module.types();
		Collection<ConstantDeclaration> conCol = module.constants();
		Collection<MethodDeclaration> modCol = module.methods();
		if (this.debugFlag) {
			contents += "// WYIL module count of types: " + typCol.size() + "\n";
			contents += "// WYIL module count of constants: " + conCol.size() + "\n";
			contents += "// WYIL module count of methods: " + modCol.size() + "\n";
		}
		//System.err.println("milestone 2.");
		cnt = 0;
		for (TypeDeclaration td : typCol) {
			cnt += 1;
			tmp = this.writeTypeComments(td, cnt);
			if (this.debugFlag) {
				contents += tmp;
			}
			contents += this.writeTypeCode(td, cnt);

		}
		//System.err.println("milestone 3.");
		cnt = 0;
		for (ConstantDeclaration cd : conCol) {
			cnt += 1;
			//contents += this.writeConstant(cd, cnt);
			tmp = this.writeConstantComments(cd, cnt);
			if (this.debugFlag) {
				contents += tmp;
			}
			contents += this.writeConstantCode(cd, cnt);

		}
		//System.err.println("milestone 4.");
		cnt = 0;
		for (MethodDeclaration md : modCol) {
			cnt += 1;
			// this.writeMethod(md, cnt);
			Method met = new Method(md, cnt);
			//contents += met.writeComments();
			tmp = met.writeComments();
			mets.add(met);
			if (this.debugFlag) {
				contents += tmp;
			}
		}
		//System.err.println("milestone 5.");
		contents += this.optIncludeFile;
		for (Method met : mets) {
			contents += met.write();
		}
		//System.err.println("milestone 6.");
		contents += this.writePostamble();
		//System.err.println("Got to end of my code.");
		return new CFile(contents);		
	}

	private String writePreamble(WyilFile module) {
		String ans = "";
		
		ans += "#line 0 \"" + module.id() + ".whiley\"" + "\n";
		ans += "// WYIL Module: " + name + "\n";
		ans += "// WYIL Filename: " + module.filename() + "\n";

		ans += this.includeFile;
		return ans;

	}

	private String writePostamble() {
		String ans = "";
		
		if (this.initor_flg == 0) {
			return ans;
		}
		ans += 		"\n";
		ans += 		"static void __initor_b() {\n";
		ans += 		"	if (wycc_debug_flag != 0)\n";
		ans += 		"		wyil_debug_str(\"initialization for " + this.name + "\");\n";
		ans += 		"	return;\n";
		ans += 		"}\n";
		ans += 		"\n";
		ans += 		"static wycc_initor __initor_c;\n";
		ans += 		"__attribute__ ((constructor)) static void __initor_a(){\n";
		ans += 		"	__initor_c.nxt = wycc_init_chain;\n";
		ans += 		"	__initor_c.function = __initor_b;\n";
		ans += 		"	wycc_init_chain = &__initor_c;\n";
		ans += 		"	return;\n";
		ans += 		"}\n";
		
		return ans;
	}

	public String writeTypeComments(TypeDeclaration typDe, int idx) {
		String lin;
		String ans = "";

		Block strain = typDe.constraint();
		List<Modifier> mods = typDe.modifiers();
		Type typ = typDe.type();
		List<Attribute> atts = typDe.attributes();

		lin = "#" + idx;
		lin += "(" + atts.size() + ":" + mods.size() + ")";
		lin += " is named " + typDe.name();
		ans += "// WYIL type declaration " + lin + "\n";
		if (typDe.isProtected()) {
			ans += "//                 is Protected\n";
		}
		if (typDe.isPublic()) {
			ans += "//                 is Public\n";
		}
		if (strain != null) {
			ans += "//                 with constraints\n";
		}
		if (typ != null) {
			ans += "//                 with a type\n";
		}
		return ans;
	}

	public String writeTypeCode(TypeDeclaration typDe, int idx) {
		return "";
	}

	public String writeConstantComments(ConstantDeclaration conDe, int idx) {
		return "// **** Need help with constant decalaration #" + idx + "\n";
	}
	
	public String writeConstantCode(ConstantDeclaration conDe, int idx) {
		return "// **** Need help with constant decalaration #" + idx + "\n";
	}


	public class Method {
		private MethodDeclaration declaration;
		private int index;
		private String body;
		private String delt; // the deconstructors
		private Map<Integer, String> declsT;
		private Map<Integer, String> declsI;
		private Set<Integer> declsU;

		private String name;
		private String indent;
		private String error;
		private boolean isNative;
		private boolean isPrivate;
		private boolean isExport;
		private boolean isProtected;
		private boolean isPublic;
		private List<Modifier> mods;
		private List<Case> cas;
		private List<Attribute> atts;
		private String comments;
		private ArrayList<Type> params;
		private Type retType;

		public Method(MethodDeclaration metDe, int idx) {
			String lin;
			int cnt;
			declaration = metDe;
			index = idx;
			name = metDe.name();
			indent = "	";
			declsT = new HashMap<Integer, String>();
			declsI = new HashMap<Integer, String>();
			declsU = new HashSet();
			Type.FunctionOrMethod rtnTyp;
			//System.err.println("milestone 5.1");
			
			error = "";
			body = "";
			isNative = false;
			isPrivate = false;
			isExport = false;
			isProtected = false;
			isPublic = false;
			mods = declaration.modifiers();
			cas = declaration.cases();
			atts = declaration.attributes();
			rtnTyp = declaration.type();
			comments = "";

			lin = "#" + index + " (";
			lin += atts.size();
			lin += ":";
			lin += mods.size();
			lin += ":";
			lin += cas.size();
			lin += ") is named " + name;
			comments += "// WYIL method declaration " + lin + "\n";
			comments += "//               with type = '"+ rtnTyp +"'\n";
			comments += typeParse(rtnTyp);
			if (declaration.isMethod()) {
				comments += "//   is method.\n";
			}
			if (declaration.isFunction()) {
				comments += "//   is function.\n";
			}
			//System.err.println("milestone 5.2");
			cnt = 0;
			for (Modifier mo : mods) {
				cnt += 1;
				comments += this.checkModifier(mo, cnt);
			}
			cnt = 0;
			for (Case ci : cas) {
				cnt += 1;
				comments += this.checkCase(ci, cnt);
			}
		}
		
		public String writeComments() {
			return comments;
		}
		
		public String typeParse(Type.FunctionOrMethod typ) {


			int cnt;
			String ans = "";
			
			params = typ.params();
			cnt = params.size();
			retType = typ.ret();
			ans += "//              return type = '" + retType+ "'\n";
			ans += "//              using '" + cnt + " parameters\n";
			return ans;
		}

		public String write() {
			String ans = "";
			int cnt;
			String sep;
			String argl = "";
			//System.err.println("milestone 5.3.1");
			cnt = 0;
			for (Case ci : cas) {
				cnt += 1;
				ans += this.writeCase(ci, cnt);
			}
			//System.err.println("milestone 5.3.12");
			if (error != "") {
				ans += "ERROR in " + name + ": ";
				ans += error;
				return ans;
			}
			if (isNative) {
				return ans;
			}
			// **** need to consider other possible types
			if (retType instanceof Type.Void) {
				ans += "void ";
			} else  {
				ans += "wycc_obj* ";
			}
			sep = "";
			cnt = 0;
			//System.err.println("milestone 5.3.3");
			for (Type tp : params){
				argl += sep + "wycc_obj* X" + cnt ;
				cnt += 1;
				sep = ", ";
			}
			ans += mungName(name) + "(" + argl + ") {\n";
			//System.err.println("milestone 5.3.4");
			ans += writeDecls();
			ans += body;
			ans += "}\n";
			//System.err.println("milestone 5.3.5");
			return ans;
		}

		private String writeDecls() {
			String ans = "";
			Integer k;
			//private Map<String, String> decl;

			for (Map.Entry<Integer, String> e : declsT.entrySet()) {
				k = e.getKey();
				ans += indent;
				ans += e.getValue();
				ans += " X" + k;
				ans += ";\n";
			}
			return ans;
		}

		private void addDecl(int target, String typ) {
			Integer tgt = target;
			String tst1 = declsT.get(tgt);
			if (tst1 == null) {
				declsT.put(tgt, typ);
			} else if (tst1.equals(typ)){
				
			} else {	
				error += "multiple type declarations for X" + target + "\n";
				
			}
		}

		public String checkModifier(Modifier mod, int idx) {
			String tag = "Unknown";

			if (mod instanceof Modifier.Export) {
				tag = "Export";
				this.isExport = true;
			} else if (mod instanceof Modifier.Native) {
				tag = "Native";
				this.isNative = true;
			} else if (mod instanceof Modifier.Private) {
				tag = "Private";
				this.isPrivate = true;
			} else if (mod instanceof Modifier.Protected) {
				tag = "Protected";
				this.isProtected = true;
			} else if (mod instanceof Modifier.Public) {
				tag = "Public";
				this.isPublic = true;
			}
			return "// modifier #" + idx + " is " + tag + "\n";
		}

		public String checkCase(Case casIn, int idx) {
			String ans = "";
			int cnt = -1;
			List<Attribute> attCol = casIn.attributes();
			Block bod = casIn.body();
			Block prec = casIn.precondition();
			Block posc = casIn.postcondition();
			List<String> locals = casIn.locals();

			if (attCol == null) {
				ans += "//           " + " no attributes\n";
			} else {
				cnt = attCol.size();
				ans += "//           " + " with " + cnt + " attributes\n";
			}
			if (prec == null) {
				ans += "//           " + " no precondition\n";
			} else {
				cnt = prec.size();
				ans += "//           " + " precondition of size " + cnt + "\n";
			}
			if (posc == null) {
				ans += "//           " + " no postcondition\n";
			} else {
				cnt = posc.size();
				ans += "//           " + " postcondition of size " + cnt + "\n";
			}
			cnt = locals.size();
			ans += "//           " + " with " + cnt + " locals\n";
			if (cnt < 1) {
				return ans;
			}
			cnt = 1;
			for (String nam : locals) {
				ans += "//           " + cnt + " '" + nam + "'\n";
				cnt += 1;
			}
			return ans;
		}

		public String writeCase(Case casIn, int idx) {
			Block bod = casIn.body();
			return this.writeBody(bod, idx);
		}

		public String writeBody(Block bodIn, int idx) {
			String ans = "";
			int cnt = -1;

			if (bodIn == null) {
				ans += "// block #" + idx + " is null\n";
				return ans;
			}
			cnt = bodIn.size();
			ans += "// block #" + idx + " is of seizes " + cnt + "\n";

			//System.err.println("milestone 5.3.1.1 - " + cnt);
			cnt = 0;			
			for (Block.Entry be : bodIn) {
				//System.err.println("milestone 5.3.1.1a : " + cnt);
				ans += this.writeBlockEntry(be, cnt);
				cnt += 1;
			}
			//System.err.println("milestone 5.3.1.9");
			return ans;
		}

		public String writeSourceLineID(Block.Entry blkIn){
			String ans = "";
			int cnt;
			
			// System.err.println("milestone 5.3.1.2a");
			List<Attribute> attCol = blkIn.attributes();
			if (attCol == null) {
				// System.err.println("milestone 5.3.1.2b");
				return "//           " + " no attributes\n";
			}
			cnt = attCol.size();
			if (cnt < 1) {
				return "//           " + " 0 attributes\n";
			}
			if (cnt != 1) {
				ans += "//           " + " with " + cnt + " attributes\n";
			}
			// System.err.println("milestone 5.3.1.2c - " + cnt);
			Attribute att = attCol.get(0);
			// System.err.println("milestone 5.3.1.2d");
			if (att instanceof Attribute.Source) {
				// System.err.println("milestone 5.3.1.2g");
				Attribute.Source attis = (Attribute.Source) att;
				// ans += "//           " + " [0] is " + attis.line + "\n";
				if (lineNumFlag) {
					// System.err.println("milestone 5.3.1.2m");
					this.body += "#line " + attis.line + "\n";
				}
				// System.err.println("milestone 5.3.1.2w");
			} else {
				// System.err.println("milestone 5.3.1.2x");
				ans += "//           " + " [0] is " + att+ "\n";
			}

			// System.err.println("milestone 5.3.1.2z");
			return ans;
		}

		//
		// convert a block entry code into some lines of C code to put in the
		// file
		// **** changes needed
		// * tracking variables declared
		// * code for variable destruction
		// * even a subclass for routines
		//
		public String writeBlockEntry(Block.Entry blkIn, int idx) {
			String ans = "";
			int targ, lhs, rhs;
			int cnt;
			String lin;
			String tag = "\t/* entry# " + idx + "*/";

			ans += "// block.entry #" + idx + "\n";
			//System.err.println("milestone 5.3.1.2");
			Code cod = blkIn.code;
			ans += this.writeSourceLineID(blkIn);

			String temp = cod.toString();
			ans += "//             Looks like " + temp + "\n";
			String[] frags = temp.split(" ", 4);
			String opc = frags[0];
			//System.err.println("milestone 5.3.1.3");
			if (cod instanceof Code.Const) {
				ans += this.writeCodeConstant(cod, tag);
			} else if (cod instanceof Code.Debug) {
				Code.Debug codd = (Code.Debug) cod;
				targ = codd.operand;
				lin = "	wyil_debug_obj(X" + targ + ");" + tag;
				this.body += lin + "\n";
			} else if (cod instanceof Code.Return) {
				ans += this.writeCodeReturn(cod, tag);
			} else if (cod instanceof Code.BinStringOp) {
				ans += this.writeCodeBinStringOp(cod, tag);

			} else if (cod instanceof Code.Assign) {
				ans += this.writeCodeAssign(cod, tag);
			} else if (cod instanceof Code.Invoke) {
				ans += this.writeCodeInvoke(cod, tag);
			} else if (cod instanceof Code.BinArithOp) {
				ans += this.writeCodeBinArithOp(cod, tag);
			} else if (cod instanceof Code.NewList) {
				ans += this.writeCodeNewList(cod, tag);
			} else if (cod instanceof Code.NewSet) {
				ans += this.writeCodeNewSet(cod, tag);
			} else if (cod instanceof Code.NewMap) {
				ans += this.writeCodeNewMap(cod, tag);

			} else if (cod instanceof Code.LengthOf) {
				ans += this.writeCodeLengthOf(cod, tag);
				
				
			} else if (cod instanceof Code.FieldLoad) {
				ans += this.writeCodeFieldLoad(cod, tag);

			} else if (cod instanceof Code.NewRecord) {
				ans += this.writeCodeNewRecord(cod, tag);

			} else if (cod instanceof Code.IndexOf) {
				ans += this.writeCodeIndexOf(cod, tag);

				
			} else if (cod instanceof Code.BinListOp) {
				ans += this.writeCodeBinListOp(cod, tag);
			} else if (cod instanceof Code.BinSetOp) {
				ans += this.writeCodeBinSetOp(cod, tag);
			} else if (cod instanceof Code.Void) {
				ans += this.writeCodeVoid(cod, tag);
			} else if (cod instanceof Code.Update) {
				ans += this.writeCodeUpdate(cod, tag);
			} else if (cod instanceof Code.UnArithOp) {
				ans += this.writeCodeUnArithOp(cod, tag);
			} else if (cod instanceof Code.TupleLoad) {
				ans += this.writeCodeTupleLoad(cod, tag);
			} else if (cod instanceof Code.TryEnd) {
				ans += this.writeCodeTryEnd(cod, tag);
			} else if (cod instanceof Code.TryCatch) {
				ans += this.writeCodeTryCatch(cod, tag);
			} else if (cod instanceof Code.Throw) {
				ans += this.writeCodeThrow(cod, tag);
			} else if (cod instanceof Code.Switch) {
				ans += this.writeCodeSwitch(cod, tag);
			} else if (cod instanceof Code.ForAll) {
				ans += this.writeCodeForAll(cod, tag);
			} else if (cod instanceof Code.If) {
				ans += this.writeCodeIf(cod, tag);
			} else if (cod instanceof Code.IfIs) {
				ans += this.writeCodeIfIs(cod, tag);
			} else if (cod instanceof Code.Loop) {
				ans += this.writeCodeLoop(cod, tag);
			} else if (cod instanceof Code.LoopEnd) {
				ans += this.writeCodeLoopEnd(cod, tag);

			} else if (cod instanceof Code.Assert) {
				ans += this.writeCodeAssert(cod, tag);

			} else if (cod instanceof Code.Void) {
				ans += this.writeCodeVoid(cod, tag);

			} else if (cod instanceof Code.Void) {
				ans += this.writeCodeVoid(cod, tag);

			} else {
				ans += "// HELP needed for opcode '" + opc + "'\n";
			}
			//System.err.println("milestone 5.3.1.8");
			return ans;
		}
	
		public String writeCodefoo(Code codIn, String tag){
			String ans = "";
			
			ans += "// HELP needed for \n";
			Code.BinSetOp cod = (Code.BinSetOp) codIn;
			return ans;
		}
		
		public String writeCodeAssert(Code codIn, String tag){
			String ans = "";
			int lhs, rhs;
			String lin;
			String cmp = "";
			
			
			ans += "// HELP needed for Assert\n";
			Code.Assert cod = (Code.Assert) codIn;
			Code.Comparator opr = cod.op;
			lhs = cod.leftOperand;
			rhs = cod.rightOperand;
			ans += "//             with " + opr + " and '" + cod.msg + "'\n";
			if (opr == Code.Comparator.ELEMOF) {
				cmp = "Wyil_Relation_Mo";
			} else if (opr == Code.Comparator.EQ){
				cmp = "Wyil_Relation_Eq";
			} else if (opr == Code.Comparator.GT){
				cmp = "Wyil_Relation_Gt";
			} else if (opr == Code.Comparator.GTEQ){
				cmp = "Wyil_Relation_Ge";
			} else if (opr == Code.Comparator.LT){
				cmp = "Wyil_Relation_Lt";
			} else if (opr == Code.Comparator.LTEQ){
				cmp = "Wyil_Relation_Le";
			} else if (opr == Code.Comparator.NEQ){
				cmp = "Wyil_Relation_Ne";

			} else if (opr == Code.Comparator.SUBSET){
				cmp = "Wyil_Relation_Ss";
			} else if (opr == Code.Comparator.SUBSETEQ){
				cmp = "Wyil_Relation_Se";

			} else {
				error += "Assert operation un-defined\n";
				ans += "// HELP needed for binListOp '" + opr + "'\n";
				return ans;
			}
			lin = "wyil_assert(X" + lhs + ", X" + rhs + ", " + cmp + ", \"" + cod.msg + "\\n\");";
			//lin = "if (X" + lhs + cmp + "X" + rhs + ") {\n";
			//lin += indent + indent + "fprintf(stderr, \"" + cod.msg + "\");\n";
			//lin += indent + indent + exit_fail;
			//lin += "};";
			this.body += indent + lin + tag + "\n";
			// addIncludeFail();
			return ans;
		}
		
		public String writeCodeNewRecord(Code codIn, String tag){
			String ans = "";
			int targ;
			int cnt;
			int idx;
			Type.Record typ;
			
			ans += "// HELP needed for NewRecord\n";
			Code.NewRecord cod = (Code.NewRecord) codIn;
			cnt = cod.operands.length;
			ans += "//             with " + cnt + " fields:\n";
			targ = cod.target;
			typ = (Type.Record) cod.type;
			ans += writeCommentRecord(typ);
			return ans;
		}

		private String writeCommentRecord(Type.Record typ){
			String ans = "";
			int idx;
			idx = 0;
			for (String ke:typ.keys()){
				idx += 1;
				ans += "//             #" + idx + ":" + ke + " " + typ.field(ke)+ "\n";
			}			
			return ans;
		}

		public String writeCodeFieldLoad(Code codIn, String tag){
			String ans = "";
			String fnam;
			Type.Record typ;
			
			ans += "// HELP needed for FieldLoad\n";
			Code.FieldLoad cod = (Code.FieldLoad) codIn;
			typ = (Type.Record) cod.type;
			fnam = cod.field;
			ans += "//             wanting field '" + fnam + "' out of:\n";
			ans += writeCommentRecord(typ) ;
			return ans;
		}
		
		public String writeCodeLoopEnd(Code codIn, String tag){
			String ans = "";
			
			ans += "// HELP needed for LoopEnd\n";
			Code.LoopEnd cod = (Code.LoopEnd) codIn;
			return ans;
		}
		
		public String writeCodeLoop(Code codIn, String tag){
			String ans = "";
	
			ans += "// HELP needed for Loop\n";
			Code.Loop cod = (Code.Loop) codIn;
			return ans;
		}
		
		public String writeCodeIfIs(Code codIn, String tag){
			String ans = "";
			
			ans += "// HELP needed for IfIs\n";
			Code.IfIs cod = (Code.IfIs) codIn;
			return ans;
		}
				
		public String writeCodeIf(Code codIn, String tag){
			String ans = "";
			
			ans += "// HELP needed for If\n";
			Code.If cod = (Code.If) codIn;
			return ans;
		}
				
		public String writeCodeForAll(Code codIn, String tag){
			String ans = "";
			
			ans += "// HELP needed for ForAll\n";
			Code.ForAll cod = (Code.ForAll) codIn;
			return ans;
		}
		
		public String writeCodeSwitch(Code codIn, String tag){
			String ans = "";
			
			ans += "// HELP needed for Switch\n";
			Code.Switch cod = (Code.Switch) codIn;
			return ans;
		}
		
		public String writeCodeThrow(Code codIn, String tag){
			String ans = "";
			
			ans += "// HELP needed for Throw\n";
			Code.Throw cod = (Code.Throw) codIn;
			return ans;
		}
		
		public String writeCodeTryCatch(Code codIn, String tag){
			String ans = "";
			
			ans += "// HELP needed for TryCatch\n";
			Code.TryCatch cod = (Code.TryCatch) codIn;
			return ans;
		}
		
		public String writeCodeTryEnd(Code codIn, String tag){
			String ans = "";
			
			ans += "// HELP needed for TryEnd\n";
			Code.TryEnd cod = (Code.TryEnd) codIn;
			return ans;
		}
		
		public String writeCodeTupleLoad(Code codIn, String tag){
			String ans = "";
			
			ans += "// HELP needed for TupleLoad\n";
			Code.TupleLoad cod = (Code.TupleLoad) codIn;
			return ans;
		}
		
		public String writeCodeUnArithOp(Code codIn, String tag){
			String ans = "";
			
			ans += "// HELP needed for \n";
			Code.UnArithOp cod = (Code.UnArithOp) codIn;
			return ans;
		}
		
		public String writeCodeUpdate(Code codIn, String tag){
			String ans = "";
			int targ, rhs, ofs;
			int cnt;
			Type typ;
			String lin;
			
			ans += "// HELP needed for Update\n";
			Code.Update cod = (Code.Update) codIn;
			targ = cod.target;
			ans += "//             target is " + targ + "\n";
			cnt = 0;
			ofs = -1;
			for (int itm : cod.operands) {
				cnt += 1;
				ans += "//             operand " + cnt + " is " + itm + "\n";
				ofs = itm;
			}
			rhs = cod.operand;
			ans += "//             rhs is " + rhs + "\n";
			typ = cod.type;
			ans += "//             type is " + typ + "\n";

			if (typ instanceof Type.List) {
				if (cnt != 1){
					error += "ERROR bad argument count for list update (" + cnt + ")\n";
				}
				lin = "X" + targ + " = wyil_update_list(X" + targ + ", X" + ofs + ", X" + rhs + ");";
				this.body += indent + lin + tag + "\n";
			} else {
				error += "ERROR cannot yet do updates for type " + typ + "\n";
			}
			return ans;
		}
		
		public String writeCodeVoid(Code codIn, String tag){
			String ans = "";
			
			ans += "// HELP needed for Void\n";
			Code.Void cod = (Code.Void) codIn;
			return ans;
		}
		
		public String writeCodeBinSetOp(Code codIn, String tag){
			String ans = "";
			
			ans += "// HELP needed for BinSetOp\n";
			Code.BinSetOp cod = (Code.BinSetOp) codIn;
			return ans;
		}

		public String writeCodeBinListOp(Code codIn, String tag){
			String ans = "";
			int targ, lhs, rhs;
			String rtn, lin;

			ans += "// HELP needed for BinListOp\n";
			Code.BinListOp cod = (Code.BinListOp) codIn;
			Code.BinListKind opr = cod.kind;
			targ = cod.target;
			ans += writeClearTarget(targ, tag);
			this.addDecl(targ, "wycc_obj*");
			lhs = cod.leftOperand;
			rhs = cod.rightOperand;
			if (opr == Code.BinListKind.APPEND) {
				rtn = "wyil_list_comb";
			} else if (opr == Code.BinListKind.LEFT_APPEND){
				rtn = "wyil_list_comb";
				error += "BinListOp ill-defined\n";
			} else if (opr == Code.BinListKind.RIGHT_APPEND){
				rtn = "wyil_list_comb";
				error += "BinListOp ill-defined\n";
			} else {
				error += "BinListOp un-defined\n";
				ans += "// HELP needed for binListOp '" + opr + "'\n";
				return ans;
			}
			lin = "X" + targ + " = " + rtn + "(X" + lhs + ", X" + rhs + ");" + tag;
			this.body += indent + lin + "\n";

			return ans;
		}

		//
		// do a lookup given a key (in a map) or an int (in a list)
		public String writeCodeIndexOf(Code codIn, String tag){
			String ans = "";
			int targ, lhs, rhs;
			String lin;
			
			// ans += "// HELP needed for IndexOf\n";
			Code.IndexOf cod = (Code.IndexOf) codIn;
			targ = cod.target;
			lhs = cod.leftOperand;
			rhs = cod.rightOperand;
			
			ans += writeClearTarget(targ, tag);
			this.addDecl(targ, "wycc_obj*");
			lin = "X" + targ + " = wyil_index_of(X" + lhs + ", X" + rhs + ");" + tag;
			this.body += indent + lin + "\n";
			return ans;
		}
		
		public String writeCodeLengthOf(Code codIn, String tag){
			String ans = "";
			int targ, rhs;
			String lin;
			
			// ans += "// HELP needed for LengthOf\n";
			Code.LengthOf cod = (Code.LengthOf) codIn;
			targ = cod.target;
			rhs = cod.operand;

			ans += writeClearTarget(targ, tag);
			this.addDecl(targ, "wycc_obj*");
			lin = "X" + targ + " = wyil_length_of(X" + rhs + ");" + tag;
			this.body += indent + lin + "\n";
			return ans;
		}

		public String writeCodeReturn(Code codIn, String tag){
			String ans = "";
			String lin;

			Code.Return cod = (Code.Return) codIn;
			
			// **** may need to consider other return types
			if (retType instanceof Type.Void) {
				lin = "	return;";
			} else  {
				lin = "	return(X" + cod.operand + ");";
			}
			this.body += lin + tag + "\n";
			return ans;
		}

		public String writeCodeNewMap(Code codIn, String tag){
			String ans = "";
			int targ;
			String lin;
			boolean flg;
			
			// ans += "// HELP needed for NewMap\n";
			Code.NewMap cod = (Code.NewMap) codIn;
			targ = cod.target;
			ans += writeClearTarget(targ, tag);
			this.addDecl(targ, "wycc_obj*");
			lin = "X" + targ + " = wycc_map_new(" + wyccTypeNone + ");" + tag;
			this.body += indent + lin + "\n";
			flg = false;
			lin = "";
			for (int itm : cod.operands) {
				if (flg) {
					lin += ", X" + itm + ");";
					this.body += indent + lin + tag + "\n";
					flg = false;
					lin = "";
				} else {
					lin = "wycc_map_add(X" + targ + ", X" + itm;
					flg = true;
				}
			}
			if (flg){
				error += "ERROR: odd count of arguments for initalizing a map\n";
			}
			return ans;
		}
				
		public String writeCodeNewSet(Code codIn, String tag){
			String ans = "";
			int cnt;
			int targ;
			String lin;

			Code.NewSet cod = (Code.NewSet) codIn;
			targ = cod.target;
			cnt = cod.operands.length;
			ans += writeClearTarget(targ, tag);
			this.addDecl(targ, "wycc_obj*");
			lin = "X" + targ + " = wycc_set_new(" + wyccTypeNone + ");" + tag;
			this.body += indent + lin + "\n";
			for (int itm : cod.operands) {
				lin = "wycc_set_add(X" + targ + ", X" + itm + ");" + tag;
				this.body += indent + lin + "\n";
			}
			return ans;
		}
		
		public String writeCodeNewList(Code codIn, String tag){
			String ans = "";
			int cnt;
			int targ;
			String lin;

			Code.NewList cod = (Code.NewList) codIn;
			targ = cod.target;
			cnt = cod.operands.length;
			ans += writeClearTarget(targ, tag);
			this.addDecl(targ, "wycc_obj*");
			lin = "X" + targ + " = wycc_list_new(" + cnt + ");" + tag;
			this.body += indent + lin + "\n";
			for (int itm : cod.operands) {
				lin = "wycc_list_add(X" + targ + ", X" + itm + ");" + tag;
				this.body += indent + lin + "\n";
			}
			return ans;
		}

		public String writeCodeBinStringOp(Code codIn, String tag){
			String ans = "";
			String rtn;
			int targ, lhs, rhs;
			String lin;
			
			Code.BinStringOp cods = (Code.BinStringOp) codIn;
			Code.BinStringKind opr = cods.kind;
			// if (opr != Code.StringOperation APPEND){
			//
			// }
			//String rtn = "wyil_strappend";
			rtn = "wyil_strappend";
			targ = cods.target;
			lhs = cods.leftOperand;
			rhs = cods.rightOperand;
			ans += writeClearTarget(targ, tag);
			lin = "X" + targ + " = " + rtn + "(X" + lhs + ", X" + rhs + ");" + tag;
			this.body += "	" + lin + "\n";
			lin = "wycc_obj* X" + targ + ";" + tag;

			this.addDecl(targ, "wycc_obj*");
			
			return ans;
		}
		
		public String writeCodeAssign(Code codIn, String tag){
			String ans = "";
			int targ, rhs;
			String lin;

			Code.Assign cod = (Code.Assign) codIn;
			targ = cod.target;
			rhs = cod.operand;
			ans += writeClearTarget(targ, tag);
			// **** should check that types match
			this.addDecl(targ, "wycc_obj*");
			lin = "X" + targ + " = X" + rhs + ";" + tag;
			this.body += indent + lin + "\n";
			lin = "X" + rhs + "->cnt++;" + tag;
			this.body += indent + lin + "\n";

			return ans;
		}
			
		public String writeCodeConvert(Code codIn, String tag){
			String ans = "";

			Code.Convert cod = (Code.Convert) codIn;
			ans += "//           ignoring convert operation \n";
			return ans;
		}
		
		public String writeCodeInvoke(Code codIn, String tag){
			String ans = "";
			int targ;
			String sep, mnam;
			String lin = "";
			
			Code.Invoke cod = (Code.Invoke) codIn;
			targ = cod.target;
			NameID nid = cod.name;
			Path.ID pat = nid.module();
			String nam = nid.name();
			mnam = defaultManglePrefix + nam;
			// ans += "// HELP for NameID '" + nam + "' within " + pat + "\n";
			if (targ >= 0) {
				ans += writeClearTarget(targ, tag);
				this.addDecl(targ, "wycc_obj*");
				lin = "X" + targ + " = ";
			}
			lin += mnam + "(";

			sep = "";
			for (int itm : cod.operands) {
				lin += sep + "X" + itm;
				sep = ", ";
			}
			lin += ");" + tag;
			this.body += indent + lin + "\n";

			return ans;
		}
		
		public String writeCodeBinArithOp(Code codIn, String tag){
			String ans = "";
			int targ, lhs, rhs;
			String rtn, lin;
			
			Code.BinArithOp cod = (Code.BinArithOp) codIn;
			Code.BinArithKind opr = cod.kind;
			targ = cod.target;
			ans += writeClearTarget(targ, tag);
			this.addDecl(targ, "wycc_obj*");
			lhs = cod.leftOperand;
			rhs = cod.rightOperand;
			// ans += "// HELP needed for binArithOp '" + opr + "'\n";
			if (opr == Code.BinArithKind.ADD) {
				rtn = "wyil_add";
			} else if (opr == Code.BinArithKind.SUB){
				rtn = "wyil_sub";
			} else if (opr == Code.BinArithKind.MUL){
				rtn = "wyil_mul";
			} else if (opr == Code.BinArithKind.DIV){
				rtn = "wyil_div";
			} else if (opr == Code.BinArithKind.REM){
				rtn = "wyil_mod";
			} else if (opr == Code.BinArithKind.BITWISEAND){
				rtn = "wyil_bit_and";
			} else if (opr == Code.BinArithKind.BITWISEOR){
				rtn = "wyil_bit_ior";
			} else if (opr == Code.BinArithKind.BITWISEXOR){
				rtn = "wyil_bit_xor";				
			} else if (opr == Code.BinArithKind.LEFTSHIFT){
				rtn = "wyil_shift_up";
			} else if (opr == Code.BinArithKind.RIGHTSHIFT){
				rtn = "wyil_shift_down";
			} else {
				ans += "// HELP needed for binArithOp '" + opr + "'\n";
				return ans;
			}
			lin = "X" + targ + " = " + rtn + "(X" + lhs + ", X" + rhs + ");" + tag;
			this.body += indent + lin + "\n";
			return ans;
		}
		
		public String writeCodeConstant(Code codIn, String tag){
			String ans = "";
			int targ;
			Constant val;
			String tyc;
			String rval;
			String assn = null;
			
			//String ctyp = "void";
			//String lin;

			Code.Const cod = (Const) codIn;
			targ = cod.target;
			val = cod.constant;
			ans += "//             target " + targ + "\n";
			tyc = val.type().toString();
			rval = val.toString();
			if (tyc.equals("string")) {
				assn = "wycc_box_cstr";								
			} else if (tyc.equals("int")) {
				assn = "wycc_box_int";
			} else if (tyc.equals("bool")) {
				assn = "wycc_box_bool";
				if (rval.equals("true")) {
					rval = "1";
				} else {
					rval = "0";
				}
			} else {
				ans += "// HELP needed for value type '" + tyc + "'\n";
				return ans;
			}
			this.addDecl(targ, "wycc_obj*");
			ans += writeClearTarget(targ, tag);
			assn += "(" + rval + ")";
			this.addDecl(targ, "wycc_obj*");
			// lin = ctyp + " X" + targ + assn + ";" + tag;
			// this.addDecl(targ, lin);
			if (assn != null) {
				this.body += indent + "X" + targ + " = " + assn + ";" + tag + "\n";
			}
			return ans;
		}
		
		public String writeClearTarget(int target, String tag){
			Integer tgt = target;
			//String ans = "";

			if (declsU.contains(tgt)) {
				this.body += indent + "wycc_deref_box(X" + target + ");" + tag + "\n";
			}
			declsU.add(tgt);
			return "";
		}
		
	}

	private String mungName(String nam) {
		String ans = this.getManglePrefix();
		ans += nam;
		return ans;
	}

}
