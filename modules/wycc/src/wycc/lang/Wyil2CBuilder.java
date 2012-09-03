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
	private List<String> fileBody;
	private Map<Integer, Type.Record> recdReg;
	private Map<String, Integer> recdTok;
	
	public Wyil2CBuilder() {
		this.debugFlag = true;
		this.lineNumFlag = true;
		this.wy2cbInit();
	}
	
	public Wyil2CBuilder(boolean dflg) {
		this.debugFlag = dflg;
		this.lineNumFlag = true;
		this.wy2cbInit();
	}
	
	public Wyil2CBuilder(Map<String, Object> values) {
		this.debugFlag = false;
		this.lineNumFlag = true;
		for (String itm : values.keySet()) {
			if (itm.equals("debug")) {
				this.debugFlag = true;
			} else if (itm.equals("no_numbers")) {
				this.lineNumFlag = false;
			}
		}
		this.wy2cbInit();
		return;
	}

	private void wy2cbInit(){
		recdReg = new HashMap<Integer, Type.Record>();
		recdTok = new HashMap<String, Integer>();
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
	
	public void bodyAddLine(String lin){
		//int ans;
		
		//ans = this.fileBody.size();
		this.fileBody.add(lin);
		return;
	}

	public void bodyAddBlock(List<String> lins){
		// int ans;
		
		//ans = this.fileBody.size();
		this.fileBody.addAll(lins);
		return;
	}

	public String bodyRender(){
		String ans = "";

		for (String itm : this.fileBody) {
			ans += itm;
		}
		return ans;
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
		String tmp;
		int cnt;
		
		this.fileBody = new ArrayList<String>();
		List<Method> mets = new ArrayList<Method>();
		//System.err.println("Got to my code.");
		this.name = module.id().toString();
		this.writePreamble(module);
		
		//System.err.println("milestone 1.");
		Collection<TypeDeclaration> typCol = module.types();
		Collection<ConstantDeclaration> conCol = module.constants();
		Collection<MethodDeclaration> modCol = module.methods();
		if (this.debugFlag) {
			tmp = "// WYIL module count of types: " + typCol.size() + "\n";
			bodyAddLine(tmp);
			tmp = "// WYIL module count of constants: " + conCol.size() + "\n";
			bodyAddLine(tmp);	
			tmp = "// WYIL module count of methods: " + modCol.size() + "\n";
			bodyAddLine(tmp);	
			
		}
		//System.err.println("milestone 2.");
		cnt = 0;
		for (TypeDeclaration td : typCol) {
			cnt += 1;
			this.writeTypeComments(td, cnt);
			this.writeTypeCode(td, cnt);

		}
		//System.err.println("milestone 3.");
		cnt = 0;
		for (ConstantDeclaration cd : conCol) {
			cnt += 1;
			this.writeConstant(cd, cnt);
		}
		//System.err.println("milestone 4.");
		cnt = 0;
		for (MethodDeclaration md : modCol) {
			cnt += 1;
			Method met = new Method(md, cnt);
			met.writeComments();
			mets.add(met);
		}
		//System.err.println("milestone 5.");

		bodyAddLine(this.optIncludeFile);
		bodyAddLine("\n");
		for (Method met : mets) {
			met.writeProto();
		}
		bodyAddLine("\n");		
		tmp = "// ===========================================";
		bodyAddLine(tmp);	
		for (Method met : mets) {
			met.write();
		}
		bodyAddLine("// \n");
		//System.err.println("milestone 6.");
		this.writePostamble();
		//System.err.println("Got to end of my code.");	
		return new CFile(this.bodyRender());
	}

	private void writePreamble(WyilFile module) {	
		//int ign;
		String tmp;
		
		if (this.lineNumFlag) {
			tmp = "#line 0 \"" + module.id() + ".whiley\"" + "\n";
			bodyAddLine(tmp);
		}
		tmp = "// WYIL Module: " + name + "\n";
		bodyAddLine(tmp);
		tmp = "// WYIL Filename: " + module.filename() + "\n";
		bodyAddLine(tmp);
		tmp = this.includeFile;
		bodyAddLine(tmp);
		tmp = "static wycc_obj *record_reg[];\n";
		bodyAddLine(tmp);

		return;
	} 

	private void writePostamble() {
		//int ign;
		String ans = "";
		
		if (this.initor_flg == 0) {
			//return ans;
			return;
		}
		bodyAddLine("\n");
		this.writeTypeRegistry();
		
		ans += 		"static void __initor_b() {\n";
		ans += 		"	if (wycc_debug_flag != 0)\n";
		ans += 		"		wyil_debug_str(\"initialization for " + this.name + "\");\n";
		bodyAddLine(ans);
		this.writeTypeRegistryFill();
		
		ans = "";
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
		bodyAddLine(ans);
		//return ans;
		return;
	}

	public int registerRecordType(Type.Record typ){
		int ans;
		String key;
		Integer tok;
		
		key = "" + typ;
		if (recdTok.containsKey(key)){
			ans = recdTok.get(key);
			return ans;
		}
		
		ans = recdTok.size();
		tok = ans;
		recdTok.put(key, tok);
		recdReg.put(tok, typ);
		return ans;
	}
	
	private void writeTypeRegistry() {
		//int ign;
		String tmp;
		int cnt = recdTok.size();
		bodyAddLine("// type registry array goes here (size " + cnt + ")\n");
		tmp = "static wycc_obj *record_reg[" + cnt + "];\n";
		bodyAddLine(tmp);
	}

	private void writeTypeRegistryFill() {
		//int ign;
		int siz;
		String tmp;
		Type.Record typ;
		int cnt = recdTok.size();
		int idx;

		bodyAddLine("// filling in type registry array goes here " + cnt + "\n");
		bodyAddLine("	wycc_obj * itm;\n");
		bodyAddLine("	wycc_obj * nam_list;\n");
		bodyAddLine("	wycc_obj * typ_list;\n");
		bodyAddLine("	wycc_obj * rcd_rcd;\n");
		idx = 0;
		for (Integer tok:recdReg.keySet()) {
			typ = recdReg.get(tok);
			siz = typ.keys().size();
			bodyAddLine("	nam_list = wycc_list_new(" + siz + ");\n");
			bodyAddLine("	typ_list = wycc_list_new(" + siz + ");\n");
			//for (String ke:typ.keys()){
			for (String ke:getFieldNames(typ)){

				bodyAddLine("	itm = wycc_box_cstr(\"" + ke + "\");\n");
				bodyAddLine("	wycc_list_add(nam_list, itm);\n");
				bodyAddLine("	itm = wycc_box_cstr(\"" + typ.field(ke) + "\");\n");
				bodyAddLine("	wycc_list_add(typ_list, itm);\n");
			}
			bodyAddLine("	rcd_rcd = wycc_record_record(nam_list, typ_list);\n");
			bodyAddLine("	record_reg[" + idx + "] = rcd_rcd;\n");
			idx+= 1;
		}
		
	}
	
	public void writeTypeComments(TypeDeclaration typDe, int idx) {
		String lin;
		//int ign;
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
		if (this.debugFlag) {
			bodyAddLine(ans);
		}
		return;
	}

	// process the file wide Type declaration
	public void writeTypeCode(TypeDeclaration typDe, int idx) {
		return;
	}

	// process the file wide Constant declaration in pass 1 comments
	public void writeConstant(ConstantDeclaration conDe, int idx) {
		String tmp;
		//int ign;
		
		if (this.debugFlag) {
			tmp = "// **** Need help with constant declaration #" + idx + "\n";
			bodyAddLine(tmp);
		}		

		tmp = "// **** Need help with constant declaaration #" + idx + "\n";
		bodyAddLine(tmp);
		return;
	}


	public class Method {
		private MethodDeclaration declaration;
		private int index;
		//private String body;
		private List<String> body;
		private String comments;
		private String delt; // the deconstructors
		private Map<Integer, String> declsT;
		private Map<Integer, String> declsI;
		private Set<Integer> declsU;
		private List<List> bStack;
		private List<String> nStack;
		
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
		private ArrayList<Type> params;
		private Type retType;
		private String proto = null;

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
			this.body = new ArrayList<String>();
			this.bStack = new ArrayList<List>();
			this.nStack = new ArrayList<String>();
			
			Type.FunctionOrMethod rtnTyp;
			//System.err.println("milestone 5.1");
			
			error = "";
			//body = "";
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

			typeParse(rtnTyp);
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
				this.checkModifier(mo, cnt);
			}
			cnt = 0;
			for (Case ci : cas) {
				cnt += 1;
				this.checkCase(ci, cnt);
			}
		}
		
		public void mbodyAddLine(String lin){
			//int ans;
			
			//ans = this.body.size();
			this.body.add(lin);
			return;
		}

		public void mbodyAddBlock(List<String> lins){
			//int ans;
			
			//ans = this.body.size();
			this.body.addAll(lins);
			return;
		}

		public void mbodyPush(String nam) {
			this.bStack.add(this.body);
			this.body = new ArrayList<String>();
			this.nStack.add(nam);
		}

		//public void mbodyPop(String nam) {
		public boolean mbodyPop(String nam) {
			int idx = this.bStack.size();
			String tgt;
			List<String> blk;
			
			//System.err.println("milestone 5.3.1.7A1");
			if (idx != this.nStack.size()){
				this.error += "name stack out-of-sync with block stack\n";
			}
			idx -= 1;
			if (idx < 0) {
				//System.err.println("ERROR: popping beyond the push.");
				return false;
			}
			tgt = this.nStack.remove(idx);
			
			if (tgt.equals(nam)){
				
			} else {
				//this.error += "target name stack mismatch: " + tgt + "!=" + nam +"\n";
				this.nStack.add(tgt);
				this.comments += "// target name stack mismatch: " + tgt + "!=" + nam +"\n";
				return false;
			}
			blk = this.bStack.remove(idx);
			for (String itm:this.body){
				if (itm.startsWith("#")) {
					blk.add(itm);
				} else {
					blk.add(indent + itm);
				}
			}
			this.body = blk;
			return true;
		}
		
		private boolean endsWithLabel(){
			int idx;
			String tmp;
			boolean ans;
			
			idx = this.body.size();
			while (true) {
				idx -= 1;
				if (idx < 0) {
					return false;
				}
				tmp = this.body.get(idx);
				if (! tmp.startsWith("#")) {
					break;
				}
			}
			ans = tmp.endsWith(":\n");
			return ans;
		}
		
		public String mbodyRender(){
			String ans = "";

			for (String itm : this.body) {
				ans += itm;
			}
			return ans;
		}
		
		//
		public void writeComments() {
			String tmp;
			//int ign;
			
			tmp = "// **** got here\n";
			bodyAddLine(tmp);
			if (debugFlag) {
				bodyAddLine(comments);
			}

			return;
		}

		public void typeParse(Type.FunctionOrMethod typ) {
			int cnt;
			String ans = "";
			
			params = typ.params();
			cnt = params.size();
			retType = typ.ret();
			ans += "//              return type = '" + retType+ "'\n";
			ans += "//              using '" + cnt + " parameters\n";
			this.comments += ans;
			return;
		}

		private void makeProto() {
			String ans = "";
			String sep = "";
			String argl = "";
			int cnt;
			
			if (proto != null) {
				return;
			}
			// **** need to consider other possible types
			if (retType instanceof Type.Void) {
				ans += "void ";
			} else  {
				ans += "wycc_obj* ";
			}
			cnt = 0;
			for (Type tp : params){
				argl += sep + "wycc_obj* X" + cnt ;
				cnt += 1;
				sep = ", ";
			}
			ans += mungName(name) + "(" + argl + ")";
			proto = ans;
			return;
		}
		
		public void writeProto() {
			String tmp;
			//int ign;
			if (isNative) {
				return;
			}
			makeProto();
			tmp = proto + ";\n";
			bodyAddLine(tmp);
			return;
		}
		
		//
		public void write() {
			String tmp;
			//int ign;
			//String ans = "";
			int cnt;
			
			//System.err.println("milestone 5.3.1");
			cnt = 0;
			for (Case ci : cas) {
				cnt += 1;
				this.writeCase(ci, cnt);
			}
			//System.err.println("milestone 5.3.2");
			if (error != "") {
				tmp = "ERROR in " + name + ": ";
				bodyAddLine(tmp);
				bodyAddLine(error);
				return;
			}
			if (isNative) {
				return;
			}
			makeProto();
			//ans += mungName(name) + "(" + argl + ") {\n";
			tmp = proto + " {\n";
			//ign = bodyAddLine(ans);
			bodyAddLine(tmp);
			//System.err.println("milestone 5.3.6");
			writeDecls();
			bodyAddBlock(body);
			tmp = "}\n";
			bodyAddLine(tmp);

			//System.err.println("milestone 5.3.8");
			return;
		}

		// private String writeDecls() {
		private void writeDecls() {
			String ans;
			Integer k;
			String typ;
			String nam = "";
			Integer skip;
			//int ign;
			//private Map<String, String> decl;

			skip = params.size();
			for (Map.Entry<Integer, String> e : declsT.entrySet()) {
				k = e.getKey();
				typ = e.getValue();
				if (k < 0) {
					nam = " XN" + (-k);
				} else if (k < skip) {
					continue;
				} else {
					nam = " X" + k;
				}
				ans = indent + typ + nam + " = (" + typ + ")0;\n";
				bodyAddLine(ans);
			}
			return;
		}

		private void addDecl(int target, String typ) {
			Integer tgt = target;
			String tst1 = declsT.get(tgt);
			if (tst1 == null) {
				declsT.put(tgt, typ);
			} else if (tst1.equals(typ)){
				
			} else {	
				this.error += "multiple type declarations for X" + target + "\n";
				
			}
		}

		// examine routine modifier, set bool variables, inject comment
		public void checkModifier(Modifier mod, int idx) {
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
			this.comments += "// modifier #" + idx + " is " + tag + "\n";
			return;
		}

		// initial examination of properties of routines
		// currently only effect is comment injection
		public void checkCase(Case casIn, int idx) {
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
			this.comments += ans;
			if (cnt < 1) {
				return;

			}
			cnt = 1;
			for (String nam : locals) {
				this.comments += "//           " + cnt + " '" + nam + "'\n";
				cnt += 1;
			}
			return;
		}

		// write case delegates to write Body
		public void writeCase(Case casIn, int idx) {
			Block bod = casIn.body();
			// return this.writeBody(bod, idx);
			this.writeBody(bod, idx);
			return;
		}

		// generate C code for the wyil sequence, each wyil byte goes to writeBlockEntry
		public void writeBody(Block bodIn, int idx) {
			//int ign;
			String tmp;
			int cnt = -1;

			if (bodIn == null) {
				tmp = "// block #" + idx + " is null\n";
				bodyAddLine(tmp);
				return;
			}
			cnt = bodIn.size();
			tmp = "// block #" + idx + " is of seizes " + cnt + "\n";
			bodyAddLine(tmp);
			
			//System.err.println("milestone 5.3.1.1 - " + cnt);
			cnt = 0;			
			for (Block.Entry be : bodIn) {
				//System.err.println("milestone 5.3.1.1a : " + cnt);
				this.writeBlockEntry(be, cnt);
				cnt += 1;
			}
			//System.err.println("milestone 5.3.1.9");
			return;
		}

		// produce comments and optionally C directives to note the source line numbers
		public String writeSourceLineID(Block.Entry blkIn){
			//int ign;
			String tmp;
			String ans = "";
			int cnt;
			int idx;
			
			//System.err.println("milestone 5.3.1.2a");
			List<Attribute> attCol = blkIn.attributes();
			if (attCol == null) {
				//System.err.println("milestone 5.3.1.2b");
				return "//           " + " no attributes\n";
			}
			//System.err.println("milestone 5.3.1.2c  : " + attCol);
			cnt = attCol.size();
			//System.err.println("milestone 5.3.1.2c1");
			if (cnt < 1) {
				//System.err.println("milestone 5.3.1.2c2");
				return "//           " + "  0 attributes\n";
			}
			if (cnt != 1) {
				ans += "//           " + " with " + cnt + " attributes\n";
			}
			//System.err.println("milestone 5.3.1.2d");
			idx = 0;
			while (idx < cnt) {
				Attribute att = attCol.get(idx);
				if (att instanceof Attribute.Source) {
					//System.err.println("milestone 5.3.1.2g");
					Attribute.Source attis = (Attribute.Source) att;
					if (lineNumFlag) {
						//System.err.println("milestone 5.3.1.2m");
						//this.body += "#line " + attis.line + "\n";
						tmp = "#line " + attis.line + "\n";
						this.mbodyAddLine(tmp);
					}
					//System.err.println("milestone 5.3.1.2w");
				} else {
					//System.err.println("milestone 5.3.1.2x");
					ans += "//           " + " [0] is " + att+ "\n";
				}
				idx++;
			}
			//System.err.println("milestone 5.3.1.2z");
			return ans;
		}

		//
		// convert a block entry code into some lines of C code to put in the
		// file
		// leading comments get passed to bodyAddLine() and the code to this.mbodyAddLine().
		// **** changes needed
		// * tracking variables declared
		// ! code for variable destruction
		// * even a subclass for routines
		//
		//public String writeBlockEntry(Block.Entry blkIn, int idx) {
		public void writeBlockEntry(Block.Entry blkIn, int idx) {
			//int ign;
			String tmp;

			String ans = "";
			int targ;
			String lin;
			String tag = "\t/* entry# " + idx + "*/";

			tmp = "// block.entry #" + idx + "\n";
			bodyAddLine(tmp);
			//System.err.println("milestone 5.3.1.2");
			Code cod = blkIn.code;
			tmp = this.writeSourceLineID(blkIn);
			bodyAddLine(tmp);
			
			String temp = cod.toString();
			tmp = "//             Looks like " + temp + "\n";
			bodyAddLine(tmp);
			
			String[] frags = temp.split(" ", 4);
			String opc = frags[0];
			//System.err.println("milestone 5.3.1.3 + " + temp);
			if (cod instanceof Code.Const) {
				ans += this.writeCodeConstant(cod, tag);
			} else if (cod instanceof Code.Debug) {
				Code.Debug codd = (Code.Debug) cod;
				targ = codd.operand;
				lin = "	wyil_debug_obj(X" + targ + ");" + tag;
				tmp = lin + "\n";
				this.mbodyAddLine(tmp);
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
			} else if (cod instanceof Code.IndexOf) {
				ans += this.writeCodeIndexOf(cod, tag);
			} else if (cod instanceof Code.Assert) {
				ans += this.writeCodeAssert(cod, tag);
			} else if (cod instanceof Code.LoopEnd) {
				ans += this.writeCodeLoopEnd(cod, tag);
			} else if (cod instanceof Code.Label) {
				ans += this.writeCodeLabel(cod, tag);
	
			} else if (cod instanceof Code.FieldLoad) {
				ans += this.writeCodeFieldLoad(cod, tag);

			} else if (cod instanceof Code.NewRecord) {
				ans += this.writeCodeNewRecord(cod, tag);
			} else if (cod instanceof Code.If) {
				ans += this.writeCodeIf(cod, tag);

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
			} else if (cod instanceof Code.IfIs) {
				ans += this.writeCodeIfIs(cod, tag);
			} else if (cod instanceof Code.Loop) {
				ans += this.writeCodeLoop(cod, tag);

			} else if (cod instanceof Code.Goto) {
				ans += this.writeCodeGoto(cod, tag);

			} else if (cod instanceof Code.Convert) {
				ans += this.writeCodeConvert(cod, tag);

			} else if (cod instanceof Code.Void) {
				ans += this.writeCodeVoid(cod, tag);
			} else if (cod instanceof Code.Void) {
				ans += this.writeCodeVoid(cod, tag);

			} else if (cod instanceof Code.Void) {
				ans += this.writeCodeVoid(cod, tag);
			} else {
				ans += "// HELP needed for opcode '" + opc + "'\n";
			}
			//System.err.println("milestone 5.3.1.8");
			bodyAddLine(ans);
			return;
		}
	
		public String writeCodefoo(Code codIn, String tag){
			String ans = "";
			
			ans += "// HELP needed for \n";
			Code.BinSetOp cod = (Code.BinSetOp) codIn;
			return ans;
		}
		
		public String writeCodeGoto(Code codIn, String tag){
			//int ign;
			String tmp;
			String target;
			
			//ans += "// HELP needed for Goto\n";
			Code.Goto cod = (Code.Goto) codIn;
			target = cod.target;
			tmp = "//             going to " + target + "\n";
			bodyAddLine(tmp);
			tmp = indent + "goto " + target + ";\n";
			this.mbodyAddLine(tmp);

			return "";
		}
		
		public String writeCodeLoopEnd(Code codIn, String tag){
			//int ign;
			String tmp;
			String nam;
			
			//tmp = "// HELP needed for LoopEnd\n";
			//bodyAddLine(tmp);

			Code.LoopEnd cod = (Code.LoopEnd) codIn;
			nam = cod.label;
			tmp = "//             called " + nam + "\n";
			bodyAddLine(tmp);
			if (this.mbodyPop(nam)) {
				if (this.endsWithLabel()) {
					tmp = indent + indent + ";\n";
					this.mbodyAddLine(tmp);
				}
				tmp = indent + "};\n";
				this.mbodyAddLine(tmp);
			}
			tmp = nam + ":\n";
			this.mbodyAddLine(tmp);
			return "";
		}

		public String writeCodeLabel(Code codIn, String tag){
			//int ign;
			String tmp;
			String nam;

			//System.err.println("milestone 5.3.1.7.1");
			tmp = "// HELP needed for Label\n";
			bodyAddLine(tmp);
			Code.Label cod = (Code.Label) codIn;
			nam = cod.label;
			tmp = "//             called " + nam + "\n";
			bodyAddLine(tmp);
			//this.mbodyPop(nam);
			//tmp = indent + "};\n";
			//if (this.mbodyPop(nam)) {
			//	tmp = indent + "};\n";
			//	ign = this.mbodyAddLine(tmp);
			//}
			tmp = nam + ":\n";
			this.mbodyAddLine(tmp);
			return "";
		}
		
		public String writeCodeAssert(Code codIn, String tag){
			//int ign;
			String tmp;
			//String ans = "";
			int lhs, rhs;
			String lin;
			String cmp;
			
			
			// ans += "// HELP needed for Assert\n";
			Code.Assert cod = (Code.Assert) codIn;
			Code.Comparator opr = cod.op;
			lhs = cod.leftOperand;
			rhs = cod.rightOperand;
			// ans += "//             with " + opr + " and '" + cod.msg + "'\n";
			cmp = mapComparator(opr, false);
			if (cmp == null) {
				error += "Assert operation un-defined\n";
				//ans += "// HELP needed for binListOp '" + opr + "'\n";
				tmp = "// HELP needed for binListOp '" + opr + "'\n";
				bodyAddLine(tmp);
				//return ans;
				return "";
			}
			lin = "wyil_assert(X" + lhs + ", X" + rhs + ", " + cmp + ", \"" + cod.msg + "\\n\");";
			//lin = "if (X" + lhs + cmp + "X" + rhs + ") {\n";
			//lin += indent + indent + "fprintf(stderr, \"" + cod.msg + "\");\n";
			//lin += indent + indent + exit_fail;
			//lin += "};";
			// this.body += indent + lin + tag + "\n";
			tmp = indent + lin + tag + "\n";
			this.mbodyAddLine(tmp);
			// addIncludeFail();
			//return ans;
			return "";
		}

		public String writeCodeIf(Code codIn, String tag){
			//int ign;
			String tmp;
			int lhs, rhs;
			String cmp;
			String target;
			

			//tmp = "// HELP needed for If\n";
			//bodyAddLine(tmp);
			Code.If cod = (Code.If) codIn;
			lhs = cod.leftOperand;
			rhs = cod.rightOperand;
			Code.Comparator opr = cod.op;
			target = cod.target;
			// cmp = mapComparator(opr, true);
			cmp = mapComparator(opr, false);
			if (cmp == null) {
				error += "Assert operation un-defined\n";
				tmp = "// HELP needed for binListOp '" + opr + "'\n";
				bodyAddLine(tmp);
				return "";				
			}

			tmp = "//             comparing X" + lhs + " " + opr + " X" + rhs + "\n";
			bodyAddLine(tmp);
			tmp = "//             going to " + target + "\n";
			bodyAddLine(tmp);
			
			tmp = indent + "if (wycc_compare(X" + lhs + ", X" + rhs + ", " + cmp + ")){\n";
			this.mbodyAddLine(tmp);
			tmp = indent + indent + "goto " + target + ";\n";
			this.mbodyAddLine(tmp);
			tmp = indent + "};\n";
			this.mbodyAddLine(tmp);
			//this.mbodyPush(target);
			return "";
		}
		
		public String writeCodeIfIs(Code codIn, String tag){
			String tmp;
			//String ans = "";
			int lhs;
			Type rhs;
			String target;
			
			//ans += "// HELP needed for IfIs\n";
			tmp = "// HELP needed for IfIs\n";
			bodyAddLine(tmp);
			Code.IfIs cod = (Code.IfIs) codIn;
			lhs = cod.operand;
			rhs = cod.rightOperand;
			target = cod.target;
			
			tmp = "//             checking if X" + lhs + " is of type " + rhs + "\n";
			bodyAddLine(tmp);
			tmp = "//             going to " + target + "\n";
			bodyAddLine(tmp);
			
			tmp = indent + "if (wycc_type_check(X" + lhs + ", \"" + rhs + "\")){\n";
			this.mbodyAddLine(tmp);
			tmp = indent + indent + "goto " + target + ";\n";
			this.mbodyAddLine(tmp);
			tmp = indent + "};\n";
			this.mbodyAddLine(tmp);
			
			//return ans;
			return "";
		}

		public String writeCodeLoop(Code codIn, String tag){
			//int ign;
			String tmp;
			String target;
	
			//tmp = "// HELP needed for Loop\n";
			//bodyAddLine(tmp);
			Code.Loop cod = (Code.Loop) codIn;
			target = cod.target;
			tmp = "//             going to " + target + "\n";
			bodyAddLine(tmp);
			tmp = indent + "while (1) {\n";
			this.mbodyAddLine(tmp);
			this.mbodyPush(target);
			return "";
		}
		
		public String writeCodeNewRecord(Code codIn, String tag){
			//int ign;
			String tmp;
			int targ;
			int cnt;
			int idx;
			int tok;
			String lin;
			Type.Record typ;
			
			tmp = "// HELP needed for NewRecord\n";
			bodyAddLine(tmp);
			Code.NewRecord cod = (Code.NewRecord) codIn;
			cnt = cod.operands.length;
			targ = cod.target;
			typ = (Type.Record) cod.type;
			tok = registerRecordType(typ);
			
			tmp = "//             tok " + tok + " with " + cnt + " fields:\n";
			bodyAddLine(tmp);
			
			
			tmp = writeCommentRecord(typ);
			bodyAddLine(tmp);
			idx = 0;
			for (int itm : cod.operands) {
				idx += 1;
				tmp = "//             operand " + idx + " is " + itm + "\n";
				bodyAddLine(tmp);
				//ofs = itm;
			}
			
			writeClearTarget(targ, tag);
			this.addDecl(targ, "wycc_obj*");
			//lin = "X" + targ + " = wycc_rrecord_new(" + cnt + ");" + tag;
			
			lin = "X" + targ + " = wycc_record_new(record_reg[" + tok + "]);" + tag;
			//this.body += indent + lin + "\n";
			tmp = indent + lin + "\n";
			this.mbodyAddLine(tmp);
			//lin = "";
			idx = 0;
			for (int itm : cod.operands) {
				lin = "wycc_record_fill(X" + targ + ", " + idx + ", X" + itm + ");";
				tmp = indent + lin + tag + "\n";
				this.mbodyAddLine(tmp);
				idx += 1;
			}
			return "";
		}

		private String writeCommentRecord(Type.Record typ){
			String ans = "";
			int idx;
			idx = 0;
			//for (String ke:typ.keys()){
			for (String ke:getFieldNames(typ)){
			
				idx += 1;
				ans += "//             #" + idx + ":" + ke + " " + typ.field(ke)+ "\n";
			}			
			return ans;
		}

		public String writeCodeFieldLoad(Code codIn, String tag){
			//int ign;
			String tmp;
			//String ans = "";
			String fnam;
			int cnt;
			int targ, rhs;
			Type.Record typ;
			
			// ans += "// HELP needed for FieldLoad\n";
			tmp = "// HELP needed for FieldLoad\n";
			bodyAddLine(tmp);
			Code.FieldLoad cod = (Code.FieldLoad) codIn;
			typ = (Type.Record) cod.type;
			targ = cod.target;
			fnam = cod.field;
			rhs = cod.operand;
			
			tmp = "//             wanting field '" + fnam + "' out of:\n";
			bodyAddLine(tmp);
			tmp = writeCommentRecord(typ);
			bodyAddLine(tmp);
			
			//cnt = 0;
			//for (String ke:typ.keys()){
				//ans += "//             #" + idx + ":" + ke + " " + typ.field(ke)+ "\n";
				//if (ke.equals(fnam)) {
					//break;
				//}
				//cnt += 1;
			//}	
			cnt = getFieldNames(typ).indexOf(fnam);
			tmp = " = wycc_record_get_dr(X" + rhs + ", " + cnt + ");";
			writeTargetSwap(tmp, targ, rhs, tag);
			
			//return ans;
			return "";
		}
				
		public String writeCodeForAll(Code codIn, String tag){
			//int ign;
			String tmp;
			int opIdx;
			int opBlk;
			String target;
			
			//tmp = "// HELP needed for ForAll\n";
			//bodyAddLine(tmp);
			Code.ForAll cod = (Code.ForAll) codIn;
			opIdx = cod.indexOperand;
			opBlk = cod.sourceOperand;
			target = cod.target;
			tmp = "//                 stepping over X" + opBlk + " with X" + opIdx + "\n";
			bodyAddLine(tmp);
			tmp = "//                 reaching" + target + "\n";
			bodyAddLine(tmp);
			writeClearTarget(opIdx, tag);
			this.addDecl(opIdx, "wycc_obj*");
			writeClearTarget(-opIdx, tag);
			this.addDecl(-opIdx, "wycc_obj*");
			
			tmp = indent + "XN" + opIdx + " = wycc_iter_new(X" + opBlk + ");\n";
			this.mbodyAddLine(tmp);
			tmp = indent + "while (X" + opIdx + " = wycc_iter_next(XN" + opIdx + ")) {\n";
			this.mbodyAddLine(tmp);
			this.mbodyPush(target);

			return "";
		}
		
		public String writeCodeSwitch(Code codIn, String tag){
			String tmp;
			//String ans = "";
			ArrayList<Pair<Constant, String>> branches;
			String target;
			int opr;
			int cnt;
			int idx;
			String alt;
			Constant con;
			
			//ans += "// HELP needed for Switch\n";
			tmp = "// HELP needed for Switch\n";
			bodyAddLine(tmp);
			Code.Switch cod = (Code.Switch) codIn;
			branches = cod.branches;
			target = cod.defaultTarget;
			opr = cod.operand;
			
			cnt = branches.size();
			tmp = "//             checking X" + opr + " with " + cnt + " choices:\n";
			bodyAddLine(tmp);
			idx = 0;
			for (Pair<Constant, String> pick:branches) {
				alt = pick.second();
				con = pick.first();
				tmp = "//             #" + idx + " -> " + con + ":" + alt + "\n";
				bodyAddLine(tmp);
				idx += 1;
				
				tmp = this.writeMyConstant(con, -opr, tag);
				if (tmp == null){
					continue;
				}
				this.mbodyAddLine(tmp);
				tmp = indent + "if (wycc_compare(X" + opr + ", XN" + opr + ", Wyil_Relation_Eq)){\n";
				this.mbodyAddLine(tmp);
				tmp = indent + indent + "goto " + alt + ";\n";
				this.mbodyAddLine(tmp);
				tmp = indent + "};\n";
				this.mbodyAddLine(tmp);
				
			}
			tmp = indent + "goto " + target + ";\n";
			this.mbodyAddLine(tmp);
			
			//return ans;
			return "";
		}
		
		public String writeCodeThrow(Code codIn, String tag){
			String tmp;
			//String ans = "";
			
			//ans += "// HELP needed for Throw\n";
			tmp = "// HELP needed for Throw\n";
			bodyAddLine(tmp);
			Code.Throw cod = (Code.Throw) codIn;
			//return ans;
			return "";
		}
		
		public String writeCodeTryCatch(Code codIn, String tag){
			String tmp;
			//String ans = "";
			
			//ans += "// HELP needed for TryCatch\n";
			tmp = "// HELP needed for TryCatch\n";
			bodyAddLine(tmp);
			Code.TryCatch cod = (Code.TryCatch) codIn;
			//return ans;
			return "";
		}
		
		public String writeCodeTryEnd(Code codIn, String tag){
			String tmp;
			//String ans = "";
			
			//ans += "// HELP needed for TryEnd\n";
			tmp = "// HELP needed for TryEnd\n";
			bodyAddLine(tmp);
			Code.TryEnd cod = (Code.TryEnd) codIn;
			//return ans;
			return "";
		}
		
		public String writeCodeTupleLoad(Code codIn, String tag){
			String tmp;
			//String ans = "";
			
			//ans += "// HELP needed for TupleLoad\n";
			tmp = "// HELP needed for TupleLoad\n";
			bodyAddLine(tmp);
			Code.TupleLoad cod = (Code.TupleLoad) codIn;
			//return ans;
			return "";
		}
		
		public String writeCodeUnArithOp(Code codIn, String tag){
			String tmp;
			//String ans = "";
			
			//ans += "// HELP needed for UnArithOp\n";
			tmp = "// HELP needed for UnArithOp\n";
			bodyAddLine(tmp);
			Code.UnArithOp cod = (Code.UnArithOp) codIn;
			//return ans;
			return "";
		}
		
		public String writeCodeUpdate(Code codIn, String tag){
			//int ign;
			String tmp;
			int targ, rhs, ofs;
			int cnt;
			Type typ;
			String lin;
			ArrayList<String> flds;
			
			tmp = "// HELP needed for Update\n";
			bodyAddLine(tmp);
			Code.Update cod = (Code.Update) codIn;
			targ = cod.target;
			flds = cod.fields;
			
			tmp = "//             target is " + targ + "\n";
			bodyAddLine(tmp);
			tmp = "//             field count is " + flds.size() + "\n";
			bodyAddLine(tmp);
			cnt = 0;
			ofs = -1;
			for (int itm : cod.operands) {
				cnt += 1;
				tmp = "//             operand " + cnt + " is " + itm + "\n";
				bodyAddLine(tmp);
				ofs = itm;
			}
			rhs = cod.operand;
			tmp = "//             rhs is " + rhs + "\n";
			bodyAddLine(tmp);
			typ = cod.type;
			tmp = "//             type is " + typ + "\n";
			bodyAddLine(tmp);

			if (typ instanceof Type.List) {
				if (cnt != 1){
					error += "ERROR bad argument count for list update (" + cnt + ")\n";
				}
				lin = "X" + targ + " = wyil_update_list(X" + targ + ", X" + ofs + ", X" + rhs + ");";
				tmp = indent + lin + tag + "\n";
				this.mbodyAddLine(tmp);
			} else if (typ instanceof Type.Strung) {
				if (cnt != 1){
					error += "ERROR bad argument count for string update (" + cnt + ")\n";
				}
				lin = "X" + targ + " = wyil_update_string(X" + targ + ", X" + ofs + ", X" + rhs + ");";
				tmp = indent + lin + tag + "\n";
				this.mbodyAddLine(tmp);
			} else if (typ instanceof Type.Map) {
				if (cnt != 1){
					error += "ERROR bad argument count for map update (" + cnt + ")\n";
				}
				lin = "wycc_map_add(X" + targ + ", X" + ofs + ", X" + rhs + ");";
				tmp = indent + lin + tag + "\n";
				this.mbodyAddLine(tmp);
			} else if (typ instanceof Type.Record) {
				if (flds.size() != 1){
					error += "ERROR bad field count for record update (" + flds.size() + ")\n";
				}
				//ofs = this.deadReconFromField(flds.get(0), (Type.Record) cod.type);
				ofs = getFieldNames((Type.Record) cod.type).indexOf(flds.get(0));
				lin = "wycc_record_fill(X" + targ + ", " + ofs + ", X" + rhs + ");";
				tmp = indent + lin + tag + "\n";
				this.mbodyAddLine(tmp);
			} else {
				error += "ERROR cannot yet do updates for type " + typ + "\n";
			}
			return "";
		}
		
		private int deadReconFromField(String fnam, Type.Record typ){
			int idx;
			idx = 0;
			for (String ke:typ.keys()){
				if (ke.equals(fnam)) {
					return idx;
				}
				idx += 1;
			}			
			
			return -1;
		}
		
		public String writeCodeVoid(Code codIn, String tag){
			String tmp;
			//String ans = "";
			
			//ans += "// HELP needed for Void\n";
			tmp = "// HELP needed for Void\n";
			bodyAddLine(tmp);
			Code.Void cod = (Code.Void) codIn;
			//return ans;
			return "";
		}
		
		public String writeCodeBinSetOp(Code codIn, String tag){
			//int ign;
			String tmp;
			int targ, lhs, rhs;
			String rtn, lin;
			
			//tmp = "// HELP needed for BinSetOp\n";
			//bodyAddLine(tmp);
			Code.BinSetOp cod = (Code.BinSetOp) codIn;
			Code.BinSetKind opr = cod.kind;
			targ = cod.target;
			lhs = cod.leftOperand;
			rhs = cod.rightOperand;
			
			writeClearTarget(targ, tag);
			this.addDecl(targ, "wycc_obj*");
			
			if (opr == Code.BinSetKind.DIFFERENCE) {
				rtn = "wyil_set_diff";
			} else if (opr == Code.BinSetKind.INTERSECTION){
				rtn = "wyil_set_insect";
			} else if (opr == Code.BinSetKind.LEFT_DIFFERENCE){
				rtn = "wyil_set_diff_left";
			} else if (opr == Code.BinSetKind.LEFT_INTERSECTION){
				rtn = "wyil_set_insect_left";
			} else if (opr == Code.BinSetKind.LEFT_UNION){
				rtn = "wyil_set_union_left";
			} else if (opr == Code.BinSetKind.RIGHT_INTERSECTION){
				rtn = "wyil_set_insect_right";
			} else if (opr == Code.BinSetKind.RIGHT_UNION){
				rtn = "wyil_set_union_right";
			} else if (opr == Code.BinSetKind.UNION){
				rtn = "wyil_set_union";
			} else {
				error += "BinSetOp un-defined\n";
				tmp = "// HELP needed for binSetOp '" + opr + "'\n";
				bodyAddLine(tmp);
				return "";
			}
			lin = "X" + targ + " = " + rtn + "(X" + lhs + ", X" + rhs + ");" + tag;
			tmp = indent + lin + "\n";
			this.mbodyAddLine(tmp);

			return "";
		}

		public String writeCodeBinListOp(Code codIn, String tag){
			//int ign;
			String tmp;
			//String ans = "";
			int targ, lhs, rhs;
			String rtn, lin;

			//ans += "// HELP needed for BinListOp\n";
			//tmp = "// HELP needed for BinListOp\n";
			//bodyAddLine(tmp);
			Code.BinListOp cod = (Code.BinListOp) codIn;
			Code.BinListKind opr = cod.kind;
			targ = cod.target;
			//ans += writeClearTarget(targ, tag);
			writeClearTarget(targ, tag);
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
				//ans += "// HELP needed for binListOp '" + opr + "'\n";
				tmp = "// HELP needed for binListOp '" + opr + "'\n";
				bodyAddLine(tmp);
				//return ans;
				return "";
			}
			lin = "X" + targ + " = " + rtn + "(X" + lhs + ", X" + rhs + ");" + tag;
			tmp = indent + lin + "\n";
			this.mbodyAddLine(tmp);
			//return ans;
			return "";
		}

		//
		// do a lookup given a key (in a map) or an int (in a list)
		public String writeCodeIndexOf(Code codIn, String tag){
			//int ign;
			String tmp;
			int targ, lhs, rhs;
			String lin;
			
			Code.IndexOf cod = (Code.IndexOf) codIn;
			targ = cod.target;
			lhs = cod.leftOperand;
			rhs = cod.rightOperand;
			
			writeClearTarget(targ, tag);
			this.addDecl(targ, "wycc_obj*");
			lin = "X" + targ + " = wyil_index_of(X" + lhs + ", X" + rhs + ");" + tag;
			tmp = indent + lin + "\n";
			this.mbodyAddLine(tmp);
			return "";
		}
		
		public String writeCodeLengthOf(Code codIn, String tag){
			//int ign;
			//String tmp;
			int targ, rhs;
			String lin;
			
			Code.LengthOf cod = (Code.LengthOf) codIn;
			targ = cod.target;
			rhs = cod.operand;
			lin = " = wyil_length_of(X" + rhs + ");";
			writeTargetSwap(lin, targ, rhs, tag);

			
			return "";
		}

		private void writeTargetSwap(String lin, int targ, int opr, String tag){
			//int ign;
			String tmp;
			if (targ == opr) {
				//writeClearTarget(-targ, tag);
				this.addDecl(-targ, "wycc_obj*");
				lin = "XN" + targ + lin + tag;
				tmp = indent + lin + "\n";
				this.mbodyAddLine(tmp);
			
				writeClearTarget(targ, tag);
				this.addDecl(targ, "wycc_obj*");
				lin = "X" + targ + " = XN" + targ + ";" + tag;
				tmp = indent + lin + "\n";
				this.mbodyAddLine(tmp);
			} else {
				writeClearTarget(targ, tag);
				lin = "X" + targ + lin + tag;
				tmp = indent + lin + "\n";
				this.mbodyAddLine(tmp);
			}
			return;
		}
		
		public String writeCodeReturn(Code codIn, String tag){
			//int ign;
			String tmp;
			String lin;

			Integer k;
			//String typ;
			String nam = "";
			Integer skip;
			int tgt;

			Code.Return cod = (Code.Return) codIn;
			
			if (retType instanceof Type.Void) {
				lin = "	return;";
				tgt = 0;
			} else  {
				tgt = cod.operand;
				skip = params.size();
				for (Map.Entry<Integer, String> e : declsT.entrySet()) {
					k = e.getKey();
					//typ = e.getValue();
					if (k < skip) {
						continue;
					} if (tgt == k) {
						continue;
					} else {
						nam = " X" + k;
					}
					tmp = indent + nam + " = wycc_deref_box(" + nam + ");\n";
					this.mbodyAddLine(tmp);
				}		
				lin = "	return(X" + tgt + ");";
			}
			// **** need to deref all the other registers.
			
			// **** may need to consider other return types
			tmp = lin + tag + "\n";
			this.mbodyAddLine(tmp);
			return "";
		}

		public String writeCodeNewMap(Code codIn, String tag){
			//int ign;
			String tmp;
			int targ;
			String lin;
			boolean flg;
			
			// ans += "// HELP needed for NewMap\n";
			//tmp = "// HELP needed for NewMap\n";
			//bodyAddLine(tmp);		
			Code.NewMap cod = (Code.NewMap) codIn;
			targ = cod.target;
			writeClearTarget(targ, tag);
			this.addDecl(targ, "wycc_obj*");
			lin = "X" + targ + " = wycc_map_new(" + wyccTypeNone + ");" + tag;
			tmp = indent + lin + "\n";
			this.mbodyAddLine(tmp);
			flg = false;
			lin = "";
			for (int itm : cod.operands) {
				if (flg) {
					lin += ", X" + itm + ");";
					//this.body += indent + lin + tag + "\n";
					tmp = indent + lin + tag + "\n";
					this.mbodyAddLine(tmp);
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
			return "";
		}
				
		public String writeCodeNewSet(Code codIn, String tag){
			//int ign;
			String tmp;
			//String ans = "";
			int cnt;
			int targ;
			String lin;

			//tmp = "// HELP needed for NewSet\n";
			//bodyAddLine(tmp);		
			Code.NewSet cod = (Code.NewSet) codIn;
			targ = cod.target;
			cnt = cod.operands.length;
			//ans += writeClearTarget(targ, tag);
			writeClearTarget(targ, tag);
			this.addDecl(targ, "wycc_obj*");
			lin = "X" + targ + " = wycc_set_new(" + wyccTypeNone + ");" + tag;
			tmp = indent + lin + "\n";
			this.mbodyAddLine(tmp);
			for (int itm : cod.operands) {
				lin = "wycc_set_add(X" + targ + ", X" + itm + ");" + tag;
				//this.body += indent + lin + "\n";
				tmp = indent + lin + "\n";
				this.mbodyAddLine(tmp);
			}
			//return ans;
			return "";
		}
		
		public String writeCodeNewList(Code codIn, String tag){
			//int ign;
			String tmp;
			//String ans = "";
			int cnt;
			int targ;
			String lin;

			//tmp = "// HELP needed for NewList\n";
			//bodyAddLine(tmp);		
			Code.NewList cod = (Code.NewList) codIn;
			targ = cod.target;
			cnt = cod.operands.length;
			//ans += writeClearTarget(targ, tag);
			writeClearTarget(targ, tag);
			this.addDecl(targ, "wycc_obj*");
			lin = "X" + targ + " = wycc_list_new(" + cnt + ");" + tag;
			tmp = indent + lin + "\n";
			this.mbodyAddLine(tmp);
			for (int itm : cod.operands) {
				lin = "wycc_list_add(X" + targ + ", X" + itm + ");" + tag;
				tmp = indent + lin + "\n";
				this.mbodyAddLine(tmp);
			}
			//return ans;
			return "";
		}

		public String writeCodeBinStringOp(Code codIn, String tag){
			//int ign;
			String tmp;
			//String ans = "";
			String rtn;
			int targ, lhs, rhs;
			String lin;
			
			Code.BinStringOp cods = (Code.BinStringOp) codIn;
			Code.BinStringKind opr = cods.kind;

			rtn = "wyil_strappend";
			targ = cods.target;
			lhs = cods.leftOperand;
			rhs = cods.rightOperand;
			//ans += writeClearTarget(targ, tag);
			writeClearTarget(targ, tag);
			lin = "X" + targ + " = " + rtn + "(X" + lhs + ", X" + rhs + ");" + tag;
			tmp = "	" + lin + "\n";
			this.mbodyAddLine(tmp);
			lin = "wycc_obj* X" + targ + ";" + tag;

			this.addDecl(targ, "wycc_obj*");
			
			//return ans;
			return "";
		}
		
		public String writeCodeAssign(Code codIn, String tag){
			//int ign;
			String tmp;
			//String ans = "";
			int targ, rhs;
			String lin;

			Code.Assign cod = (Code.Assign) codIn;
			targ = cod.target;
			rhs = cod.operand;
			//ans += writeClearTarget(targ, tag);
			writeClearTarget(targ, tag);
			// **** should check that types match
			this.addDecl(targ, "wycc_obj*");
			lin = "X" + targ + " = X" + rhs + ";" + tag;
			tmp = indent + lin + "\n";
			this.mbodyAddLine(tmp);
			//lin = "X" + rhs + "->cnt++;" + tag;
			lin = "WY_OBJ_BUMP(X" + rhs + ");" + tag;
			tmp = indent + lin + "\n";
			this.mbodyAddLine(tmp);
			//return ans;
			return "";
		}
			
		public String writeCodeConvert(Code codIn, String tag){
			//int ign;
			String tmp;
			int tgt, opr;
			Type ntyp, otyp;

			Code.Convert cod = (Code.Convert) codIn;
			tgt = cod.target;
			opr = cod.operand;
			ntyp = cod.result;
			otyp = cod.type;
			tmp = "//            ignoring convert operation \n";
			bodyAddLine(tmp);
			tmp = "//            change X" + opr + " from " + otyp + " to " + ntyp + "\n";
			bodyAddLine(tmp);
			return "";
		}
		
		public String writeCodeInvoke(Code codIn, String tag){
			//int ign;
			String tmp;
			//String ans = "";
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
				//ans += writeClearTarget(targ, tag);
				writeClearTarget(targ, tag);
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
			tmp = indent + lin + "\n";
			this.mbodyAddLine(tmp);
			//return ans;
			return "";
		}
		
		public String writeCodeBinArithOp(Code codIn, String tag){
			//int ign;
			String tmp;
			//String ans = "";
			int targ, lhs, rhs;
			String rtn, lin;
			
			Code.BinArithOp cod = (Code.BinArithOp) codIn;
			Code.BinArithKind opr = cod.kind;
			targ = cod.target;
			//ans += writeClearTarget(targ, tag);
			writeClearTarget(targ, tag);
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
			} else if (opr == Code.BinArithKind.RANGE){
				rtn = "wyil_range";
			} else {
				//ans += "// HELP needed for binArithOp '" + opr + "'\n";
				tmp = "// HELP needed for binArithOp '" + opr + "'\n";
				bodyAddLine(tmp);
				//return ans;
				return "";
			}
			lin = "X" + targ + " = " + rtn + "(X" + lhs + ", X" + rhs + ");" + tag;
			tmp = indent + lin + "\n";
			this.mbodyAddLine(tmp);
			//return ans;
			return "";
		}
		
		public String writeCodeConstant(Code codIn, String tag){
			//int ign;
			String tmp;
			int targ;
			Constant val;
			Type typ;
			String rval;
			String assn = null;
			
			Code.Const cod = (Const) codIn;
			targ = cod.target;
			val = cod.constant;
			tmp = "//             target " + targ + "\n";
			bodyAddLine(tmp);
			tmp = this.writeMyConstant(val, targ, tag);
			if (tmp == null) {
				return "";
			}
			this.addDecl(targ, "wycc_obj*");
			writeClearTarget(targ, tag);
			
			this.mbodyAddLine(tmp);

			return "";
		}

		private String writeMyConstant(Constant val, int targ, String tag){
			String tmp;
			// int targ;
			// Constant val;
			Type typ;
			String rval;
			String assn = null;
			
			// Code.Const cod = (Const) codIn;
			// targ = cod.target;
			// val = cod.constant;
			// tmp = "//             target " + targ + "\n";
			// bodyAddLine(tmp);
			typ = val.type();
			rval = val.toString();
			if (typ instanceof Type.Strung) {	
				assn = "wycc_box_cstr(" + rval + ")";							
			} else if (typ instanceof Type.Int) {
				assn = "wycc_box_int(" + rval + ")";
			} else if (typ instanceof Type.Bool) {
				if (rval.equals("true")) {
					rval = "1";
				} else {
					rval = "0";
				}
				assn = "wycc_box_bool(" + rval + ")";
			} else if (typ instanceof Type.Char) {
				assn = "wycc_box_char(" + rval + ")";
			} else if (typ instanceof Type.Set) {
				assn = "wycc_set_new(-1)";
			} else if (typ instanceof Type.Map) {
				assn = "wycc_map_new(-1)";
			} else if (typ instanceof Type.Null) {
				assn = "wycc_box_null()";
			} else {
				tmp = "// HELP needed for value type '" + typ + "'\n";
				bodyAddLine(tmp);
				return null;
			}
			//this.addDecl(targ, "wycc_obj*");
			//writeClearTarget(targ, tag);
			// assn += "(" + rval + ")";
			// this.addDecl(targ, "wycc_obj*");
			if (assn != null) {
				tmp = indent + "X" + targ + " = " + assn + ";" + tag + "\n";
				//this.mbodyAddLine(tmp);
			} else {
				tmp = "";
			}
			return tmp;
		}
		
		// A register is about to be clobbered; dereference any object.
		// negative register numbers are our own constructs, very local, not ref counted.
		public void writeClearTarget(int target, String tag){
			//int ign;
			String tmp;
			String nam = "";
			Integer tgt = target;


			if (declsU.contains(tgt)) {
				if (target < 0) {
					nam = "XN" + (-target);
				} else {
					nam = "X" + target;
					tmp = indent + nam + " = wycc_deref_box(" + nam + ");" + tag + "\n";
					this.mbodyAddLine(tmp);
				}
				//tmp = indent + "wycc_deref_box(X" + target + ");" + tag + "\n";
				//tmp = indent + "wycc_deref_box(" + nam + ");" + tag + "\n";
				//tmp = indent + nam + " = wycc_deref_box(" + nam + ");" + tag + "\n";
				//ign = this.mbodyAddLine(tmp);
			}
			declsU.add(tgt);
			//return "";
			return;
		}

		
		// convert a wyil comparator code to a wycc constant
		private String mapComparator(Code.Comparator opr, boolean flg) {
			
			if (opr == Code.Comparator.ELEMOF) {
				return "Wyil_Relation_Mo";
			} else if (opr == Code.Comparator.EQ){
				return "Wyil_Relation_Eq";
			} else if (opr == Code.Comparator.GT){
				return "Wyil_Relation_Gt";
			} else if (opr == Code.Comparator.GTEQ){
				return "Wyil_Relation_Ge";
			} else if (opr == Code.Comparator.LT){
				return "Wyil_Relation_Lt";
			} else if (opr == Code.Comparator.LTEQ){
				return "Wyil_Relation_Le";
			} else if (opr == Code.Comparator.NEQ){
				return "Wyil_Relation_Ne";
			}
			if (flg) {
				return null;
			} 
			if (opr == Code.Comparator.SUBSET){
				return "Wyil_Relation_Ss";
			} else if (opr == Code.Comparator.SUBSETEQ){
				return "Wyil_Relation_Se";

			} 
			return null;
		}		
	}

		private ArrayList<String> getFieldNames(Type.Record record) {
		    ArrayList<String> fields = new ArrayList<String>(record.keys());
		    Collections.sort(fields);
		    return fields;
		}

	
	
	private String mungName(String nam) {
		String ans = this.getManglePrefix();
		ans += nam;
		return ans;
	}

}
