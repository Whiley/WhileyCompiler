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
import java.math.BigInteger;

import wyautl_old.lang.Automaton;
import wyautl_old.lang.Automaton.State;
import wybs.lang.Attribute;
import wybs.lang.Builder;
import wybs.lang.Logger;
import wybs.lang.NameID;
import wybs.lang.NameSpace;
import wybs.lang.Path;
import wyil.lang.WyilFile;

import wybs.lang.Path;
import wybs.util.Pair;
import wyil.lang.Block;
import wyil.lang.Code;
import wyil.lang.Code.Const;
import wyil.lang.Code.BinStringKind;
import wyil.lang.Code.Dereference;
import wyil.lang.Code.LVal;
import wyil.lang.Constant.Bool;
import wyil.lang.Modifier;
import wyil.lang.Type;
import wyil.lang.Constant;
import wyil.lang.Type.Record;
import wyil.lang.Type.Strung;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Case;
import wyil.lang.WyilFile.ConstantDeclaration;
import wyil.lang.WyilFile.MethodDeclaration;
import wyil.lang.WyilFile.TypeDeclaration;

public class Wyil2CBuilder implements Builder {
	// * these are configuration parameters of the translation process
	private final String defaultManglePrefix = "wycc__";
	private final String includeFile = "#include \"wycc_lib.h\"";

	// * these are parameters of the translation process from invocation

	private String name;
	private boolean debugFlag;			// generate output and code to help diagnose the compiler
	private boolean lineNumFlag;		// add line number directives in the C to map it to the Whiley source
	private boolean floatFlag;			// produce floating point code to approximate the rationals
	private boolean floatForceFlag;
	private boolean indirectForceFlag;	// force all calls to use indirect references.

	// * these are details of the c implementation
	private final int wyccTypeAny = 0;
	private final int wyccTypeNone = -1;
	private final String exit_fail = "exit(-4);";

	//* these are derived or intermediate parameters
	private Logger logger = Logger.NULL;
	private String optIncludeFile = "";
	private String manglePrefix = null;
	private int ourFOMstate;			// index of first external	
	private int initor_flg = 1;
	
	// * these are structures for recording characteristics of the code.
	private List<String> fileBody;
	// special handling needed for both record objects and FOM objects.
	// each type of record and each FOM has an integer token assigned.
	// FOM objects can be overloaded, so the type must be known for calling.
	// However, all variant types in the standard libraries share one file per name.
	private Map<Integer, Type.Record> recdReg;
	private Map<String, Integer> recdTok;
	private Map<Integer, String> fomReg;
	private Map<String, Integer> fomTok;
	private Set<String> fomNames;
	private List<Method> mets;			// the collection of our structures for handling FOMs in this source
	private Set<String> faultCodes;
	//private String commentsFOM = "";
	
	public Wyil2CBuilder() {
		this.wy2cbInit();
	}
	
	public Wyil2CBuilder(boolean dflg) {
		this.wy2cbInit();
		this.debugFlag = dflg;
	}
	
	public Wyil2CBuilder(Map<String, Object> values) {
		this.wy2cbInit();
		this.debugFlag = false;
		this.lineNumFlag = true;
		for (String itm : values.keySet()) {
			if (itm.equals("debug")) {
				this.debugFlag = true;
			} else if (itm.equals("no_numbers")) {
				this.lineNumFlag = false;
			} else if (itm.equals("floats")) {
				this.floatFlag = true;
				this.floatForceFlag = true;
			} else if (itm.equals("no_floats")) {
				this.floatFlag = false;
				this.floatForceFlag = true;
			} else if (itm.equals("only_indirect_calls")) {
				this.lineNumFlag = true;
			}

		}

		return;
	}

	private void wy2cbInit(){
		// * construct all the empty data structures for recording code details during processing
		//System.err.println("Got to my init code.");
		recdReg = new HashMap<Integer, Type.Record>();
		recdTok = new HashMap<String, Integer>();
		fomReg = new HashMap<Integer, String>();
		fomTok = new HashMap<String, Integer>();
		fomNames = new HashSet<String>();
		
		faultCodes = new HashSet<String>();
		faultCodes.add("invoke");
		faultCodes.add("throw");
		
		// * initalise the invocation flags and the starting states
		this.debugFlag = true;
		this.lineNumFlag = true;
		this.floatFlag = true;		// the default setting
		this.floatForceFlag = false;	// no user choice made.
		this.indirectForceFlag = false;	// the default setting
		this.ourFOMstate = -1;	// the initial setting

	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public NameSpace namespace() {
		return null; // TODO: **** this seems like a mistake in Builder ?
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
				//System.err.println("Processing .... ");
				Path.Entry<WyilFile> sf = (Path.Entry<WyilFile>) p.first();
				Path.Entry<CFile> df = (Path.Entry<CFile>) f;
				// build the C-File
				CFile contents = build(sf.read());								
				// finally, write the file into its destination
				df.write(contents);
			} else {
				//System.err.println("Skipping .... " + f.contentType());
			}
		}

		// ========================================================================
		// Done
		// ========================================================================

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Wyil => C: compiled " + delta.size()
				+ " file(s)", endTime - start, memory - runtime.freeMemory());
	}	

	// combine the name and type of a FOM and return a token (index in structures) for the pair.
	public int lookupNumFOMname(String nam, Type typ) {
		int ans;
		String dtyp;
		String key;
		Integer tok;
		
		// **** need to fill in the invocation register
		dtyp = writeDenseType(typ);
		//commentsFOM += "// FOM query for " + nam + "  '" + dtyp + "'\n";

		key = nam + " " + dtyp;
		if (fomTok.containsKey(key)){
			tok = fomTok.get(key);
			ans = tok;
		} else {
			ans = fomTok.size();
			tok = ans;
			fomTok.put(key, tok);
			fomReg.put(tok, key);
			fomNames.add(nam);
		}
		bodyAddLineNL(	"// FOM query #" + ans	);
		return ans;
	}
	
	//mnam = lookupFOMname(nam, cnt, cod.type);
	public String lookupFOMname(String nam, Type typ) {
		int alt;

		alt = lookupNumFOMname(nam, typ);
		bodyAddLineNL(	"// FOM query #" + alt	);
		if (ourFOMstate < 0) {
			return getManglePrefix() + nam + "__" + alt;
		} else if (alt < ourFOMstate) {
			return getManglePrefix() + nam + "__" + alt;
		} else {
			return getManglePrefix() + nam;
		}
	}
	
	public void bodyAddLine(String lin){

		this.fileBody.add(lin);
		return;
	}
	
	public void bodyAddLineNL(String lin){

		this.fileBody.add(lin + "\n");
		return;
	}

	public void bodyAddBlock(List<String> lins){

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
		int cnt;
		
		this.fileBody = new ArrayList<String>();
		this.mets = new ArrayList<Method>();
		//System.err.println("Got to my code.");
		this.name = module.id().toString();
		this.writePreamble(module);
		
		//System.err.println("milestone 1.");
		Collection<TypeDeclaration> typCol = module.types();
		Collection<ConstantDeclaration> conCol = module.constants();
		Collection<MethodDeclaration> modCol = module.methods();
		
		// * at this point we are thru with the aggregate module; now to process separate collections
		if (this.debugFlag) {
			bodyAddLineNL(	"// WYIL module count of types: " + typCol.size()	);
			bodyAddLineNL(	"// WYIL module count of constants: " + conCol.size()	);
			bodyAddLineNL(	"// WYIL module count of methods: " + modCol.size()	);	
			
		}

		cnt = 0;
		for (TypeDeclaration td : typCol) {
			cnt += 1;
			this.writeTypeComments(td, cnt);
			this.writeTypeCode(td, cnt);

		}

		cnt = 0;
		for (ConstantDeclaration cd : conCol) {
			cnt += 1;
			this.writeConstant(cd, cnt);
		}

		cnt = 0;
		for (MethodDeclaration md : modCol) {
			cnt += 1;
			Method met = new Method(md, cnt);
			met.writeComments();
			mets.add(met);
		}

		// * done processing module and its component collections ; now to spit out C code
		// * completion of preamble is function/method prototypes
		bodyAddLine(this.optIncludeFile);
		bodyAddLineNL(	""	);
		for (Method met : mets) {
			met.writeProto();
		}
		bodyAddLineNL(	""	);
		bodyAddLineNL(	"// ==========================================="	);	// mark end of preamble
		ourFOMstate = fomTok.size();
		for (Method met : mets) {
			met.write();
		}
		//bodyAddLineNL(	"//"	);
		bodyAddLineNL(	"// ==========================================="	);	// mark start of postamble

		this.writePostamble();
		//System.err.println("Got to end of my code.");	
		return new CFile(this.bodyRender());
	}

	// * write generic preamble
	private void writePreamble(WyilFile module) {
		
		if (this.lineNumFlag) {
			bodyAddLineNL(	"#line 0 \"" + module.id() + ".whiley\""	);
		}
		bodyAddLineNL(	"// WYIL Module: " + name	);
		bodyAddLineNL(	"// WYIL Filename: " + module.filename()	);
		bodyAddLineNL(	this.includeFile	);
		bodyAddLineNL(	"static wycc_obj *record_reg[];"	);
		bodyAddLineNL(	"static wycc_obj *fom_handle_reg[];"	);
		return;
	} 

	private void writePostamble() {
		
		if (this.initor_flg == 0) {
			return;
		}
		bodyAddLineNL("");
		
		this.writeTypeRegistry();
		this.writeFOMRegistry();
		
		bodyAddLineNL(		""													);
		bodyAddLineNL(		"static void __initor_b() {"						);
		bodyAddLineNL( 		"	if (wycc_debug_flag != 0)"						);
		bodyAddLineNL( 		"		wyil_debug_str(\"registering for " + this.name + "\\n\");"	);
		this.writeTypeRegistryFill();
		this.writeFOMRegistryFill();
				
		bodyAddLineNL( 		"	return;"								);
		bodyAddLineNL( 		"}"											);
		
		bodyAddLineNL(		""													);
		bodyAddLineNL(		"static void __initor_d() {"							);
		bodyAddLineNL(		"	if (wycc_debug_flag != 0)"						);
		bodyAddLineNL(		"		wyil_debug_str(\"consulting for " + this.name + "\\n\");"	);
		
		this.writeFOMRegistryQuery();

		bodyAddLineNL(		"	return;"										);
		bodyAddLineNL(		"}"													);
		
		
		bodyAddLineNL(		""													);
		bodyAddLineNL(		"static wycc_initor __initor_c;"					);
		bodyAddLineNL( 		"__attribute__ ((constructor)) static void __initor_a() {"		);
		bodyAddLineNL( 		"	__initor_c.nxt = wycc_init_chain;"				);
		bodyAddLineNL(		"	__initor_c.functionr = __initor_b;"				);
		bodyAddLineNL( 		"	__initor_c.functionq = __initor_d;"				);
		bodyAddLineNL(		"	wycc_init_chain = &__initor_c;"					);
		bodyAddLineNL( 		"	return;"										);
		bodyAddLineNL( 		"}"													);

		this.writeFOMLinkTrigger();
		return;
	}

	// * generate triggers to link in needed library routines ; most will be impedance matched at runtime
	private void writeFOMLinkTrigger() {
		Set<String> nams = new HashSet<String>();
		String tmp;
		
		bodyAddLineNL(	"// FOM Link Trigger references go here."	);
		bodyAddLineNL(	"static void* wyccTriggers[] = {"	);
		for (String nam : fomNames){
			nams.add(nam);
		}
		for (Method met : mets) {
			if (! met.isNative) {
				tmp = met.name;
				if (nams.contains(tmp))
					nams.remove(tmp);
			}
		}
		tmp = "";
		for (String nam : nams){ 
			//bodyAddLineNL(	"// " + nam	);
			if (tmp != "") {
				bodyAddLineNL(	tmp + ","	);
			}
			tmp ="	(void *) " + getManglePrefix() + nam;
		}
		bodyAddLineNL(	tmp	);
		bodyAddLineNL(	"};"	);
		
	}

	// * given a record type return a token (index) to refer to that type later.
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
		
		int cnt = recdTok.size();
		bodyAddLineNL(	"// type registry array goes here (size " + cnt + ")"	);
		bodyAddLineNL(	"static wycc_obj *record_reg[" + cnt + "];"	);
	}

	private void writeFOMRegistry() {
		
		int cnt = fomTok.size();
		bodyAddLineNL(	"// FOM handle registry array goes here (size " + cnt + ")"	);
		bodyAddLineNL(	"static wycc_obj *fom_handle_reg[" + cnt + "];"	);
	}

	private void writeTypeRegistryFill() {
		String dtyp;

		Type.Record typ;
		int cnt = recdTok.size();
		int idx;

		bodyAddLineNL("// filling in type registry array goes here " + cnt	);
		bodyAddLineNL("	wycc_obj * rcd_rcd;"	);
		idx = 0;
		for (Integer tok:recdReg.keySet()) {
			typ = recdReg.get(tok);
			dtyp = writeDenseType(typ);
			bodyAddLineNL("	record_reg[" + idx + "] = wycc_record_type(\"" + dtyp + "\");"	);
			idx+= 1;
		}
	}

	private void writeFOMRegistryFill() {
		String tmp;

		bodyAddLineNL("// Here goes code to fill the FOM registry");
		// need a loop over the known FOM.
		
		for (Method met : mets) {
			tmp = "	wycc_register_routine(\"" + met.name ;
			tmp += "\", \"" + met.denseType;
			tmp += "\", " + met.altName + ");";
			bodyAddLineNL(	tmp	);
		}
	}
	
	private void writeFOMRegistryQuery() {
		String key;
		int idx;
		String nam;
		String dty;
		int cnt = 0;
		
		//System.err.println("milestone 99a");
		bodyAddLineNL("// Here goes code to query the FOM registry");
		//bodyAddLine(commentsFOM);
		for (Integer tok:fomReg.keySet()) {
			key = fomReg.get(tok);
			idx = key.indexOf(' ');
			nam = key.substring(0,idx);
			dty = key.substring(idx+1);
			bodyAddLineNL("	fom_handle_reg[" + cnt + "] = wycc_fom_handle(\"" + nam + "\", \"" + dty + "\");"	);
			cnt += 1;
		}

	}
	
	public void writeTypeComments(TypeDeclaration typDe, int idx) {
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
		
		if (this.debugFlag) {
			tmp = "// **** Need help with constant declaration #" + idx;
			bodyAddLineNL(tmp);
		}		

		tmp = "// **** Need help with constant declaaration #" + idx;
		bodyAddLineNL(tmp);
		if (conDe.isProtected()) {
			tmp = "//                 is Protected";
			bodyAddLineNL(tmp);
		}
		if (conDe.isPublic()) {
			tmp = "//                 is Public";
			bodyAddLineNL(tmp);
		}
		tmp = "//                 has name '" + conDe.name() + "'";
		bodyAddLineNL(tmp);
		return;
	}

	// * a class to hold every detail we need about FOMs (Function Or Method)
	public class Method {
		private MethodDeclaration declaration;
		private int index;
		//private String body;
		private List<String> body;
		private String comments;
		// private String delt; // the deconstructors
		private Map<Integer, String> declsT;
		private Map<Integer, String> declsI;
		private Set<Integer> declsU;
		private List<List<String>> bStack;			// a stack of stacks of lines (a context body)
		private List<String> nStack;		// a stack of names for end-of-s 
		private List<String> tStack;		// a try stack (names of end labels)
		private List<Integer> tvStack;		// a try stack (register number for the exception object)
		private List<ArrayList<Pair<Type, String>>> tcStack; 
		
		private String indent;
		private String sourceTag;
		private String error;
		private boolean isNative;
		private boolean isPrivate;
		private boolean isExport;
		private boolean isProtected;
		private boolean isPublic;
		private boolean returnSeen;
		private List<Modifier> mods;
		private List<Case> cas;
		private List<Attribute> atts;
		private ArrayList<Type> params;
		private String proto = null;
		
		public String name;
		public String altName;
		public Type retType;
		public int argc;
		public String argt;
		public String denseType;
		public int indexFOM;

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
			this.bStack = new ArrayList<List<String>>();
			this.nStack = new ArrayList<String>();
			this.tcStack = new ArrayList<ArrayList<Pair<Type, String>>>  ();
			this.tvStack = new ArrayList<Integer>  ();
			
			Type.FunctionOrMethod rtnTyp;
			
			error = "";
			isNative = false;
			isPrivate = false;
			isExport = false;
			isProtected = false;
			isPublic = false;
			returnSeen = false;
			mods = declaration.modifiers();
			cas = declaration.cases();
			atts = declaration.attributes();
			rtnTyp = declaration.type();
			denseType = writeDenseType(rtnTyp);
			indexFOM = lookupNumFOMname(name, rtnTyp);
			altName = getManglePrefix() + name + "__" + indexFOM;
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
		
		private void mbodyAddLine(String lin){
			this.body.add(lin);
			return;
		}
		
		private void mbodyAddLineNL(String lin){
			this.body.add(lin + "\n");
			return;
		}

		private void mbodyAddLineTINL(String lin){
			this.body.add(	indent + lin + this.sourceTag + "\n"	);
			return;
		}

		private void mbodyAddLineINL(String lin){
			this.body.add(	indent + lin + "\n"	);
			return;
		}
		
		private void mbodyAddBlock(List<String> lins){
			this.body.addAll(lins);
			return;
		}

		//  save a context for nested code
		private void mbodyPush(String nam) {
			this.bStack.add(this.body);
			this.body = new ArrayList<String>();
			this.nStack.add(nam);
		}

		// collapse the current context into the next save down.
		private boolean mbodyPop(String nam) {
			int idx = this.bStack.size();
			String tgt;
			List<String> blk;
			
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
				
		//
		public void writeComments() {

			if (debugFlag) {
				bodyAddLine(comments);
			}
			return;
		}

		public void typeParse(Type.FunctionOrMethod typ) {
			String ans = "";
			String argl = "";
			String sep = "";
			
			params = typ.params();
			this.argc = params.size();
			retType = typ.ret();
			ans += "//              return type = '" + retType+ "'\n";
			ans += "//              using '" + this.argc + " parameters\n";
			this.comments += ans;
			for (Type tp : params){
				argl += sep + tp;
				sep = "^";
			}
			this.argt = argl;
			return;
		}

		private void makeProto() {
			String ans = "static ";
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

			ans += altName + "(" + argl + ")";
			proto = ans;
			return;
		}
		
		public void writeProto() {
			if (isNative) {
				return;
			}
			makeProto();
			bodyAddLineNL(	proto + ";"	);
			return;
		}
		
		//
		public void write() {
			int cnt;
			
			cnt = 0;
			for (Case ci : cas) {
				cnt += 1;
				this.writeCase(ci, cnt);
			}
			if (error != "") {
				bodyAddLineNL(	"ERROR in " + name + ": "	);
				bodyAddLine(	error	);
				return;
			}
			if (isNative) {
				return;
			}
			makeProto();
			bodyAddLineNL(	proto + " {"	);
			writeDecls();
			bodyAddBlock(body);
			bodyAddLineNL(	"}"	);

			return;
		}

		private void writeDecls() {
			String ans;
			Integer k;
			String typ;
			String nam = "";
			Integer skip;
			int cnt;

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
				ans = indent + typ + nam + " = (" + typ + ")0;";
				bodyAddLineNL(ans);
			}
			//ans = indent + "wycc_obj* Xc = (wycc_obj*)0;";
			//bodyAddLineNL(ans);
			//ans = indent + "wycc_obj* Xb = (wycc_obj*)0;";
			//bodyAddLineNL(ans);
			//ans = indent + "wycc_obj* Xa = (wycc_obj*)0;";
			//bodyAddLineNL(ans);
			//ans = indent + "wycc_obj** Xi = (wycc_obj**)0;";
			//bodyAddLineNL(ans);
			
			bodyAddLineNL(	indent + "wycc_obj* Xc = (wycc_obj*)0;"	);;
			bodyAddLineNL(	indent + "wycc_obj* Xb = (wycc_obj*)0;"	);
			bodyAddLineNL(	indent + "wycc_obj* Xa = (wycc_obj*)0;"	);
			bodyAddLineNL(	indent + "wycc_obj** Xi = (wycc_obj**)0;"	);
			cnt = 0;
			//for (Type tp : params){
			while (cnt < skip) {
				bodyAddLineNL(	indent + "WY_OBJ_BUMP(X" + cnt + ");"	);
				cnt += 1;
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
			//Block bod = casIn.body();
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
			this.writeBody(bod, idx);
			return;
		}

		// generate C code for the wyil sequence, each wyil byte goes to writeBlockEntry
		public void writeBody(Block bodIn, int idx) {
			int cnt = -1;

			if (bodIn == null) {
				bodyAddLineNL(	"// block #" + idx + " is null"	);
				return;
			}
			cnt = bodIn.size();
			bodyAddLineNL(	"// block #" + idx + " is of seizes " + cnt	);

			cnt = 0;			
			for (Block.Entry be : bodIn) {
				this.writeBlockEntry(be, cnt);
				cnt += 1;
			}
			return;
		}

		// produce comments and optionally C directives to note the source line numbers
		public String writeSourceLineID(Block.Entry blkIn){
			String ans = "";
			int cnt;
			int idx;
			
			List<Attribute> attCol = blkIn.attributes();
			if (attCol == null) {
				return "//           " + " no attributes\n";
			}
			cnt = attCol.size();
			if (cnt < 1) {
				return "//           " + "  0 attributes\n";
			}
			if (cnt != 1) {
				ans += "//           " + " with " + cnt + " attributes\n";
			}
			idx = 0;
			while (idx < cnt) {
				Attribute att = attCol.get(idx);
				if (att instanceof Attribute.Source) {
					Attribute.Source attis = (Attribute.Source) att;
					if (lineNumFlag) {
						this.mbodyAddLineNL(	"#line " + attis.line	);
					}
				} else {
					ans += "//           " + " [0] is " + att+ "\n";
				}
				idx++;
			}
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
		public void writeBlockEntry(Block.Entry blkIn, int idx) {
			String tmp;
			int targ;
			//String lin;
			String tag = "\t/* entry# " + idx + "*/";

			this.sourceTag = tag;
			bodyAddLineNL(	"// block.entry #" + idx	);
			Code cod = blkIn.code;
			tmp = this.writeSourceLineID(blkIn);
			bodyAddLine(tmp);
			
			String temp = cod.toString();
			bodyAddLineNL(	"//             Looks like " + safeString2(temp)	);

			String[] frags = temp.split(" ", 4);
			String opc = frags[0];
			if (cod instanceof Code.Const) {
				this.writeCodeConstant(cod);
			} else if (cod instanceof Code.Debug) {
				Code.Debug codd = (Code.Debug) cod;
				targ = codd.operand;
				//lin = "	wyil_debug_obj(X" + targ + ");" + tag;
				//this.mbodyAddLineNL(	lin	);
				this.mbodyAddLineTINL(	"wyil_debug_obj(X" + targ + ");"	);
			} else if (cod instanceof Code.Return) {
				this.writeCodeReturn(cod);
			} else if (cod instanceof Code.BinStringOp) {
				this.writeCodeBinStringOp(cod);

			} else if (cod instanceof Code.Assign) {
				this.writeCodeAssign(cod);
			} else if (cod instanceof Code.Invoke) {
				this.writeCodeInvoke(cod);
			} else if (cod instanceof Code.BinArithOp) {
				this.writeCodeBinArithOp(cod);
			} else if (cod instanceof Code.NewList) {
				this.writeCodeNewList(cod);
			} else if (cod instanceof Code.NewSet) {
				this.writeCodeNewSet(cod);
			} else if (cod instanceof Code.NewMap) {
				this.writeCodeNewMap(cod);
			} else if (cod instanceof Code.LengthOf) {
				this.writeCodeLengthOf(cod);
			} else if (cod instanceof Code.IndexOf) {
				this.writeCodeIndexOf(cod);
			} else if (cod instanceof Code.AssertOrAssume) {
				this.writeCodeAssertOrAssume(cod);
			} else if (cod instanceof Code.LoopEnd) {
				this.writeCodeLoopEnd(cod);
			} else if (cod instanceof Code.TryEnd) {
				this.writeCodeTryEnd(cod);
			} else if (cod instanceof Code.Label) {
				this.writeCodeLabel(cod);
	
			} else if (cod instanceof Code.FieldLoad) {
				this.writeCodeFieldLoad(cod);

			} else if (cod instanceof Code.NewRecord) {
				this.writeCodeNewRecord(cod);
			} else if (cod instanceof Code.If) {
				this.writeCodeIf(cod);

			} else if (cod instanceof Code.BinListOp) {
				this.writeCodeBinListOp(cod);
			} else if (cod instanceof Code.BinSetOp) {
				this.writeCodeBinSetOp(cod);
			} else if (cod instanceof Code.Void) {
				this.writeCodeVoid(cod);
			} else if (cod instanceof Code.Update) {
				this.writeCodeUpdate(cod);
			} else if (cod instanceof Code.UnArithOp) {
				this.writeCodeUnArithOp(cod);
			} else if (cod instanceof Code.TupleLoad) {
				this.writeCodeTupleLoad(cod);
			} else if (cod instanceof Code.TryCatch) {
				this.writeCodeTryCatch(cod);
			} else if (cod instanceof Code.Throw) {
				this.writeCodeThrow(cod);
			} else if (cod instanceof Code.Switch) {
				this.writeCodeSwitch(cod);
			} else if (cod instanceof Code.ForAll) {
				this.writeCodeForAll(cod);
			} else if (cod instanceof Code.IfIs) {
				this.writeCodeIfIs(cod);
			} else if (cod instanceof Code.Loop) {
				this.writeCodeLoop(cod);
			} else if (cod instanceof Code.NewTuple) {
				this.writeCodeNewTuple(cod);

			} else if (cod instanceof Code.Goto) {
				this.writeCodeGoto(cod);

			} else if (cod instanceof Code.Convert) {
				this.writeCodeConvert(cod);

			} else if (cod instanceof Code.SubList) {
				this.writeCodeSubList(cod);
			} else if (cod instanceof Code.SubString) {
				this.writeCodeSubString(cod);

			} else if (cod instanceof Code.NewObject) {
				this.writeCodeNewObject(cod);
			} else if (cod instanceof Code.Dereference) {
				this.writeCodeDereference(cod);
			} else if (cod instanceof Code.Invert) {
				this.writeCodeInvert(cod);
			} else if (cod instanceof Code.IndirectInvoke) {
				this.writeCodeIndirectInvoke(cod);
			} else if (cod instanceof Code.Lambda) {
				this.writeCodeLambda(cod);
				
			} else if (cod instanceof Code.Void) {
				this.writeCodeVoid(cod);
			} else {
				bodyAddLineNL(	"// HELP! needed for opcode '" + opc + "'"	);
			}
			if (faultCodes.contains(opc)) {
				writeCatchCheck();
			}
			return;
		}
			
		public void writeCodeVoid(Code codIn){

			bodyAddLineNL(	"// HELP! needed for Void"	);
			Code.Void cod = (Code.Void) codIn;
			return;
		}
				
		public void writeCodeLambda(Code codIn){
			int targ;
			int cnt;
			int foo;
			int tok;
			Type typ;
			String nam;
			String lin;
			String dtyp;
			String sig = "++";		// ***** must change this

			bodyAddLineNL(	"// HELP! needed for Lambda"	);
			Code.Lambda cod = (Code.Lambda) codIn;
			targ = cod.target;
			nam = cod.name.name();
			typ = cod.type;
			bodyAddLineNL(	"//		name is '" + nam + "'"	);
			bodyAddLineNL(	"//		operands are:"	);
			cnt = cod.operands.length;
			writeClearTarget(targ);
			this.addDecl(targ, "wycc_obj*");
			this.mbodyAddLineTINL(	"Xc = wycc_list_new(" + cnt + ");"	);
			cnt = 0;
			foo = -1;
			for (int itm : cod.operands) {
				cnt+=1;
				bodyAddLineNL(	"//		#" + cnt + " : " + itm	);
				if (itm < 0) {
					this.mbodyAddLineTINL(	"wycc_list_add(Xc, 0);"	);
				} else {
					this.mbodyAddLineTINL(	"wycc_list_add(Xc, X" + itm + ");"	);
				}
				if (itm == targ) {
					foo = targ;
				}
			}
			//Constant.Lambda fom = (Constant.Lambda) val;
			//bodyAddLineNL(	"// HELP! needed in lambda for FOM name: '" + fom.name.name() + "'"	);
			bodyAddLineNL(	"// HELP! needed in lambda for FOM name: '" + nam + "'"	);
			dtyp = writeDenseType(typ);
			bodyAddLineNL(	"// HELP! needed in const for FOM dtyp: '" + dtyp + "'"	);
			//tok = lookupNumFOMname(fom.name.name(), typ);
			tok = lookupNumFOMname(nam, typ);		
			lin = " = wycc_lambda_new(fom_handle_reg[" + tok + "], Xc);"	;
			writeTargetSwap(lin, targ, foo);
			
			return;
		}
				
		public void writeCodeIndirectInvoke(Code codIn){
			String tmp;
			int cnt;
			int targ;
			int opr;
			String var;
			
			Code.IndirectInvoke cod = (Code.IndirectInvoke) codIn;
			targ = cod.target;
			cnt = cod.operands.length;
			opr = cod.operand;
			
			var = "Xc";			
			this.mbodyAddLineTINL(	var + " = wycc_list_new(" + cnt + ");"	);
			cnt = -1;
			for (int itm : cod.operands) {
				this.mbodyAddLineTINL(	"wycc_list_add(" + var + ", X" + itm + ");"	);
				if (itm == targ) {
					cnt = targ;
				}
			}
			if (cnt == -1) {
				if (targ == opr) {
					cnt = opr;
				}
			}
			tmp = " = wycc_indirect_invoke(X" +opr + ", " + var + ");";
			writeTargetSwap(tmp, targ, cnt);

			return;
		}
		
		public void writeCodeInvoke(Code codIn){
			int targ;
			String sep, mnam;
			String lin = "";
			int foo;
		
			Code.Invoke cod = (Code.Invoke) codIn;
			targ = cod.target;
			NameID nid = cod.name;
			//Path.ID pat = nid.module();
			String nam = nid.name();
			mnam = lookupFOMname(nam, cod.type);
			lin += mnam + "(";

			foo = -1;
			sep = "";
			for (int itm : cod.operands) {
				lin += sep + "X" + itm;
				sep = ", ";
				if (itm == targ) {
					foo = targ;
				}
			}
			lin += ");";
			if (targ < 0) {
				this.mbodyAddLineTINL(	lin	);
				return;
			}
			lin = " = " + lin;
			writeTargetSwap(lin, targ, foo);

			return;
		}
		
		public void writeCatchCheck(){
			
			if (1 > this.tcStack.size()) {
				mbodyAddLineINL(	"if (wycc_exception_check()) goto return0;"	);
			} else {
				mbodyAddLineINL(	"if (wycc_exception_check()) break;"	);
			}
			return;
		}

		public void writeCodeTryEnd(Code codIn){
			String nam;
			Type typ;
			String dtyp;
			Integer reg;
			int opr;
			ArrayList<Pair<Type, String>> dsp;
			int idx = this.tcStack.size()-1;

			//bodyAddLineNL(	"// HELP! needed for TryEnd"	);
			Code.TryEnd cod = (Code.TryEnd) codIn;
			nam = cod.label;
			if (this.mbodyPop("")) {
				if (this.endsWithLabel()) {
					this.mbodyAddLineINL(	indent + ";"	);
				}
				this.mbodyAddLineINL(	"} while (0);"	);
				dsp = this.tcStack.remove(idx);
				reg = this.tvStack.remove(idx);
				opr = reg;

				for (Pair<Type, String> pitm: dsp) {
					typ = pitm.first();
					dtyp = writeDenseType(typ);
					this.mbodyAddLineINL(	indent + "if (wycc_exception_try(\"" + dtyp + "\")){"	);
					writeClearTarget(opr);
					this.mbodyAddLineINL(	indent + "X" + opr +" = wycc_exception_get();"	);
					this.mbodyAddLineINL(	indent + "wycc_exception_clear();"	);
					this.mbodyAddLineINL(	indent + "goto " + pitm.second() + ";"		);
					this.mbodyAddLineINL(	"};"		);
				}
				this.mbodyAddLineINL(	"goto return0;"	);
			}
			this.mbodyAddLineNL(	nam + ":"		);
			return;
		}
		
		public void writeCodeTryCatch(Code codIn){
			int lhs;
			Integer reg;
			
			//bodyAddLineNL(	"// HELP! needed for TryCatch"	);
			Code.TryCatch cod = (Code.TryCatch) codIn;
			lhs = cod.operand;
			ArrayList<Pair<Type, String>> foo = cod.catches;
			
			tcStack.add(foo);
			this.addDecl(lhs, "wycc_obj*");
			
			reg = lhs;
			tvStack.add(reg);
			this.mbodyAddLineINL(	"wyil_catch(\"" + this.name + "\");"	 );
			this.mbodyAddLineINL(	"do {"	);
			this.mbodyPush("");
			return;
		}
		
		public void writeCodeThrow(Code codIn){
			int lhs;

			bodyAddLineNL(	"// HELP! needed for Throw"		);
			Code.Throw cod = (Code.Throw) codIn;
			lhs = cod.operand;
			this.mbodyAddLineTINL(	"wyil_throw(X" + lhs + ");"	);
			return;
		}
		
		public void writeCodeDereference(Code codIn){
			String tmp;
			int targ, lhs;
			
			bodyAddLineNL(	"// HELP needed for Dereference"		);
			Code.Dereference cod = (Code.Dereference) codIn;
			targ = cod.target;
			lhs = cod.operand;
			
			tmp = " = wyil_dereference(X" + lhs  + ");";
			writeTargetSwap(tmp, targ, lhs);
			
			return;
		}

		public void writeCodeNewObject(Code codIn){
			String tmp;
			int targ, lhs;

			//bodyAddLineNL(	"// HELP needed for NewObject"	);
			Code.NewObject cod = (Code.NewObject) codIn;
			targ = cod.target;
			lhs = cod.operand;
			
			tmp = " = wycc_box_ref(X" + lhs  + ");";
			writeTargetSwap(tmp, targ, lhs);
			return;
		}

		public void writeCodeSubString(Code codIn){
			int targ;
			String lin;
			int src, lo, hi;
			int cnt;
			
			//bodyAddLineNL(	"// HELP needed for SubString"	);
			Code.SubString cod = (Code.SubString) codIn;
			targ = cod.target;
			cnt = cod.operands.length;
			if (cnt != 3) {
				error += "ERROR SubString bad arg count " + cnt + "\n";
				return;
			}
			src = cod.operands[0];
			lo = cod.operands[1];
			hi = cod.operands[2];
			writeClearTarget(targ);
			this.addDecl(targ, "wycc_obj*");
			lin = "X" + targ + " = wyil_substring(X" + src + ", X" + lo + ", X" + hi + ");";
			this.mbodyAddLineTINL(	lin	);

			return;
		}
		
		public void writeCodeNewTuple(Code codIn){
			int targ;
			int cnt;
			int idx;
			
			//bodyAddLineNL(	"// HELP needed for NewTuple"	);
			Code.NewTuple cod = (Code.NewTuple) codIn;
			targ = cod.target;
			cnt = cod.operands.length;
			
			writeClearTarget(targ);
			this.addDecl(targ, "wycc_obj*");
			this.mbodyAddLineTINL(	"X" + targ + " = wycc_tuple_new(" + cnt + ");"	);
			idx = 0;
			for (int itm : cod.operands) {
				//lin = "wycc_update_list(X" + targ + ", X" + itm + ", " + idx+ ");" + tag;
				//this.mbodyAddLineINL(	lin	);
				this.mbodyAddLineTINL(	"wycc_update_list(X" + targ + ", X" + itm + ", " + idx+ ");"	);
				idx += 1;
			}
			return;
		}
		
		public void writeCodeTupleLoad(Code codIn){
			int targ, rhs, idx;
			
			//bodyAddLineNL(	"// HELP needed for TupleLoad"	);
			Code.TupleLoad cod = (Code.TupleLoad) codIn;
			targ = cod.target;
			rhs = cod.operand;
			idx = cod.index;
			
			writeClearTarget(targ);
			this.addDecl(targ, "wycc_obj*");
			this.mbodyAddLineTINL(	"X" + targ + " = wycc_list_get(X" + rhs + ", " + idx + ");"	);
			
			return;
		}
		
		public void writeCodeInvert(Code codIn){
			String tmp;
			int targ, rhs;
			
			//bodyAddLineNL(	"// HELP! needed for Invert"	);
			Code.Invert cod = (Code.Invert) codIn;
			targ = cod.target;
			rhs = cod.operand;
			
			tmp = " = wyil_invert(X" + rhs + ");";
			writeTargetSwap(tmp, targ, rhs);
			
			return;
		}
		
		public void writeCodeGoto(Code codIn){
			String target;
			
			//bodyAddLineNL(	"// HELP needed for Goto"	);
			Code.Goto cod = (Code.Goto) codIn;
			target = cod.target;
			bodyAddLineNL(	"//             going to " + target	);
			this.mbodyAddLineINL(	"goto " + target + ";"	);

			return;
		}
		
		public void writeCodeLoopEnd(Code codIn){
			String nam;
			
			//bodyAddLineNL(	"// HELP needed for LoopEnd"	);
			Code.LoopEnd cod = (Code.LoopEnd) codIn;
			nam = cod.label;
			bodyAddLineNL(	"//             called " + nam	);
			if (this.mbodyPop(nam)) {
				if (this.endsWithLabel()) {
					this.mbodyAddLineINL(	indent + ";"	);
				}
				this.mbodyAddLineINL(	"};"	);
			}
			this.mbodyAddLineNL(	nam + ":"	);
			return;
		}

		public void writeCodeLabel(Code codIn){
			String nam;

			//bodyAddLineNL(	"// HELP needed for Label"	);
			Code.Label cod = (Code.Label) codIn;
			nam = cod.label;
			bodyAddLineNL(	"//             called " + nam	);
			this.mbodyAddLineNL(	nam + ":"	);
			return;
		}
		
		public void writeCodeAssertOrAssume(Code codIn){
			int lhs, rhs;
			String lin;
			String cmp;

			//bodyAddLineNL(	"// HELP needed for Assert"	);
			
			Code.AssertOrAssume cod = (Code.AssertOrAssume) codIn;
			Code.Comparator opr = cod.op;
			lhs = cod.leftOperand;
			rhs = cod.rightOperand;
			cmp = mapComparator(opr, false);
			if (cmp == null) {
				error += "Assert operation un-defined\n";
				bodyAddLineNL(	"// HELP needed for binListOp '" + opr + "'"	);
				return;
			}
			lin = "wyil_assert(X" + lhs + ", X" + rhs + ", " + cmp + ", \"" + cod.msg + "\\n\");";
			this.mbodyAddLineTINL(	lin	);
			return;
		}

		public void writeCodeIf(Code codIn){
			int lhs, rhs;
			String cmp;
			String target;

			//bodyAddLineNL(	"// HELP needed for If"	);
			Code.If cod = (Code.If) codIn;
			lhs = cod.leftOperand;
			rhs = cod.rightOperand;
			Code.Comparator opr = cod.op;
			target = cod.target;
			cmp = mapComparator(opr, false);
			if (cmp == null) {
				error += "Assert operation un-defined\n";
				bodyAddLineNL(	"// HELP needed for binListOp '" + opr + "'"	);
				return;				
			}
			bodyAddLineNL(	"//             comparing X" + lhs + " " + opr + " X" + rhs	);
			bodyAddLineNL(	"//             going to " + target	);

			this.mbodyAddLineINL(	"if (wycc_compare(X" + lhs + ", X" + rhs + ", " + cmp + ")){"	);
			this.mbodyAddLineINL(	indent + "goto " + target + ";"	);
			this.mbodyAddLineINL(	"};"	);
			return;
		}
		
		public void writeCodeIfIs(Code codIn){
			int lhs;
			Type rhs;
			String target;
			String dtyp;

			//bodyAddLineNL(	"// HELP needed for IfIs"	);
			Code.IfIs cod = (Code.IfIs) codIn;
			lhs = cod.operand;
			rhs = cod.rightOperand;
			target = cod.target;
			
			dtyp = writeDenseType(rhs);
			bodyAddLineNL(	"//             checking if X" + lhs + " is of type " + rhs	);
			bodyAddLineNL(	"//			or " + dtyp 	);
			bodyAddLineNL(	"//             going to " + target	);
						
			this.mbodyAddLineINL(	"if (wycc_type_check(X" + lhs + ", \"" + dtyp + "\")){"	);
			this.mbodyAddLineINL(	indent + "goto " + target + ";"	);
			this.mbodyAddLineINL(	"};"	);
			return;
		}

		public void writeCodeLoop(Code codIn){
			String target;
	
			//bodyAddLineNL(	"// HELP needed for Loop"	);
			Code.Loop cod = (Code.Loop) codIn;
			target = cod.target;
			bodyAddLineNL(	"//             going to " + target	);

			this.mbodyAddLineINL(	"while (1) {"	);
			this.mbodyPush(target);
			return;
		}
		
		public void writeCodeNewRecord(Code codIn){
			String tmp;
			int targ;
			int cnt;
			int idx;
			int tok;
			String lin;
			Type.Record typ;
	
			//bodyAddLineNL(	"// HELP needed for NewRecord"	);
			Code.NewRecord cod = (Code.NewRecord) codIn;
			cnt = cod.operands.length;
			targ = cod.target;
			typ = (Type.Record) cod.type;
			tok = registerRecordType(typ);
			
			bodyAddLineNL(	"//             tok " + tok + " with " + cnt + " fields:"	);

			tmp = writeCommentRecord(typ);
			bodyAddLine(tmp);
			idx = 0;
			for (int itm : cod.operands) {
				idx += 1;
				bodyAddLineNL(	"//             operand " + idx + " is " + itm	);

			}
			
			writeClearTarget(targ);
			this.addDecl(targ, "wycc_obj*");
			this.mbodyAddLineTINL(	"X" + targ + " = wycc_record_new(record_reg[" + tok + "]);"	);
			idx = 0;
			for (int itm : cod.operands) {
				this.mbodyAddLineTINL(	"wycc_record_fill(X" + targ + ", " + idx + ", X" + itm + ");"	);
				idx += 1;
			}
			return;
		}

		private String writeCommentRecord(Type.Record typ){
			String ans = "";
			int idx;
			idx = 0;

			for (String ke:getFieldNames(typ)){
				idx += 1;
				ans += "//             #" + idx + ":" + ke + " " + typ.field(ke)+ "\n";
			}			
			return ans;
		}

		public void writeCodeFieldLoad(Code codIn){
			String tmp;
			String fnam;
			int cnt;
			int targ, rhs;
			Type.Record typ;

			//bodyAddLineNL(	"// HELP needed for FieldLoad"	);
			Code.FieldLoad cod = (Code.FieldLoad) codIn;
			targ = cod.target;
			fnam = cod.field;
			rhs = cod.operand;
			bodyAddLineNL(	"//             wanting field '" + fnam + "' out of:"	);
			
			if (cod.type instanceof Type.Record) {
				typ = (Type.Record) cod.type;
				tmp = writeCommentRecord(typ);
				bodyAddLine(tmp);
				cnt = getFieldNames(typ).indexOf(fnam);
				tmp = " = wycc_record_get_dr(X" + rhs + ", " + cnt + ");";
				
			} else {
				tmp = " = wycc_record_get_nam(X" + rhs + ", \"" + fnam + "\");";
			}
			writeTargetSwap(tmp, targ, rhs);
			
			return;
		}
				
		public void writeCodeForAll(Code codIn){
			//String tmp;
			int opIdx;
			int opBlk;
			String target;
			
			//bodyAddLineNL(	"// HELP needed for ForAll"	);
			Code.ForAll cod = (Code.ForAll) codIn;
			opIdx = cod.indexOperand;
			opBlk = cod.sourceOperand;
			target = cod.target;
			bodyAddLineNL(	"//                 stepping over X" + opBlk + " with X" + opIdx	);
			bodyAddLineNL(	"//                 reaching" + target	);
			writeClearTarget(opIdx);
			this.addDecl(opIdx, "wycc_obj*");
			writeClearTarget(-opIdx);
			this.addDecl(-opIdx, "wycc_obj*");

			this.mbodyAddLineINL(	"XN" + opIdx + " = wycc_iter_new(X" + opBlk + ");"	);
			this.mbodyAddLineINL(	"while (X" + opIdx + " = wycc_iter_next(XN" + opIdx + ")) {"	);
			this.mbodyPush(target);

			return;
		}
		
		public void writeCodeSwitch(Code codIn){
			String tmp;
			ArrayList<Pair<Constant, String>> branches;
			String target;
			int opr;
			int cnt;
			int idx;
			String alt;
			Constant con;
			String nam;

			//bodyAddLineNL(	"// HELP needed for Switch"	);
			Code.Switch cod = (Code.Switch) codIn;
			branches = cod.branches;
			target = cod.defaultTarget;
			opr = cod.operand;
			
			cnt = branches.size();
			bodyAddLineNL(	"//             checking X" + opr + " with " + cnt + " choices:"	);
			idx = 0;
			nam = "XN" + opr;
			this.addDecl(-opr, "wycc_obj*");
			for (Pair<Constant, String> pick:branches) {
				alt = pick.second();
				con = pick.first();
				bodyAddLineNL(	"//             #" + idx + " -> " + con + ":" + alt	);
				idx += 1;

				tmp = this.writeMyConstant(con, nam, 0);
				if (tmp == null){
					continue;
				}
				this.mbodyAddLine(tmp);				
				this.mbodyAddLineINL(	"if (wycc_compare(X" + opr + ", XN" + opr + ", Wyil_Relation_Eq)){"	);
				this.mbodyAddLineINL(	indent + "goto " + alt + ";"	);
				this.mbodyAddLineINL(	"};"	);
			}			
			this.mbodyAddLineINL(	"goto " + target + ";"	);
			return;
		}
		
		public void writeCodeUnArithOp(Code codIn){
			String tmp;
			int targ, rhs;
			String rtn, lin;
			
			//bodyAddLine(	"// HELP needed for UnArithOp"	);
			Code.UnArithOp cod = (Code.UnArithOp) codIn;
			Code.UnArithKind opr = cod.kind;
			targ = cod.target;
			rhs = cod.operand;

			if (opr == Code.UnArithKind.NEG) {
				rtn = "wyil_negate";
			} else if (opr == Code.UnArithKind.NUMERATOR){
				if (floatFlag) {
					bodyAddLineNL(	"// HELP! needed for unArithOp '" + opr + "'"	);
					return;
				}
				rtn = "wyil_numer";
			} else if (opr == Code.UnArithKind.DENOMINATOR){
				if (floatFlag) {
					bodyAddLineNL(	"// HELP! needed for unArithOp '" + opr + "'"	);
					return;
				}
				rtn = "wyil_denom";
			} else {
				bodyAddLineNL(	"// HELP! needed for unArithOp '" + opr + "'"	);
				return;
			}
			tmp = " = " + rtn + "(X" + rhs + ");";
			writeTargetSwap(tmp, targ, rhs);
			
			return;
		}
		
		public void writeCodeUpdate(Code codIn){
			String tmp;
			int targ, rhs, ofs;
			int cnt;
			Type typ;
			String lin;
			String backFix;
			ArrayList<String> flds;
			Code.LVal lv;
			int idx;
			int iidx, fidx;
			String tnam1, tnam2;
			String tag = this.sourceTag;

			//
			// There is a small bit of magic here, accomplished with a group of 3 temporary variables
			// Start by realising that the composite structure must be mutable at least from the top
			// on down to the point of change.
			// The 3 variables (in the C code) are Xa, Xb, and Xc
			// Xc becomes that variable by which we reference the composite
			// Xb is the starting value for the next layer down in the composite
			// Xa gets the value of the unshared (mutable) form of Xb
			// Here backFix gets the C code to update the (soon to be) previous level
			// 
			// a Reference is not only mutable, but is expected to track the mutations (COWs)
			// to which it referes.
			//
			bodyAddLineNL(	"// HELP needed for Update"	);
			Code.Update cod = (Code.Update) codIn;
			targ = cod.target;
			flds = cod.fields;
			idx = cod.level();
			
			tmp = "//             target is " + targ + " and depth is " + idx;
			bodyAddLineNL(tmp);
			tmp = "//             field count is " + flds.size();
			bodyAddLineNL(tmp);
			cnt = 0;
			ofs = -1;
			for (int itm : cod.operands) {
				cnt += 1;
				tmp = "//             operand " + cnt + " is " + itm;
				bodyAddLineNL(tmp);
				ofs = itm;
			}
			rhs = cod.operand;
			tmp = "//             rhs is " + rhs;
			bodyAddLineNL(tmp);
			typ = cod.type;
			tmp = "//             type is " + typ;
			bodyAddLineNL(tmp);

			this.addDecl(targ, "wycc_obj*");
			tnam1 = "X" + targ;
			Iterator<Code.LVal> foo = cod.iterator();

			iidx = 0;
			fidx = 0;
			backFix = tnam1 + " = Xa;";
			this.mbodyAddLineTINL(	"Xb = " + tnam1 + ";"	);
			this.mbodyAddLineTINL(	"Xi = &(" + tnam1 + ");"	);
			
			while (idx > 0) {
				lv = foo.next();
				if (lv instanceof Code.ReferenceLVal) {
					lin = "Xc = Xb;";
					tmp = indent + lin + tag;
					tmp += " /* a */ ";
					this.mbodyAddLineNL(tmp);
					
				} else {
					lin = "Xa = wycc_cow_obj(Xb);";
					tmp = indent + lin + tag;
					tmp += " /* b */ ";
					this.mbodyAddLineNL(tmp);
					lin = "if (Xb != Xa) {";

					this.mbodyAddLineINL(	lin	);
					this.mbodyAddLineINL(	indent + backFix	);
					this.mbodyAddLineINL(	"};"	);
					this.mbodyAddLineTINL(	"Xc = Xa;"	);
				}
			
				if (lv instanceof Code.ListLVal) {
					ofs = cod.operands[iidx];
					iidx += 1;
					if (idx > 1) {
						lin = "Xb = wyil_index_of(Xc, X" + ofs + ");" + tag;
						backFix = "Xb = wyil_update_list(Xc, X" + ofs + ", Xa);" + tag;
					} else {
						lin =  "Xb = wyil_update_list(Xc, X" + ofs + ", X" + rhs + ");";
					}
					tmp = indent + lin + tag;
					tmp += " /* c */ ";
					this.mbodyAddLineNL(tmp);
					
				} else if (lv instanceof Code.StringLVal) {
					ofs = cod.operands[iidx];
					iidx += 1;
					if (idx > 1) {
						error += "ERROR cannot do updates below a string\n";
						return;
					} else {
						lin = "Xb = wyil_update_string(Xc, X" + ofs + ", X" + rhs + ");";
					}
					tmp = indent + lin + tag;
					tmp += " /* d */ ";
					this.mbodyAddLineNL(tmp);

				} else if (lv instanceof Code.ReferenceLVal) {
					if (idx > 1) {
						lin = "Xb = wyil_dereference(Xc);" + tag;
						//backFix = "Xb = wycc_box_ref(Xa);" + tag;
						//backFix = "/* ugly no-op */;" + tag;
						//backFix = "if (Xi) { *Xi = wycc_box_ref(Xa); Xi = (wycc_obj**)0;};" + tag;
						backFix = "wycc_update_ref(Xc, Xa);";
					} else {
						//lin = tnam1 + " = wyil_update_string(" + tnam1 + ", X" + ofs + ", X" + rhs + ");";
						//lin = "Xb = wyil_update_string(Xc, X" + ofs + ", X" + rhs + ");";
						error += "ERROR cannot do updates at a reference\n";
						return;
					}
					tmp = indent + lin + tag;
					tmp += " /* e */ ";
					this.mbodyAddLineNL(tmp);

				} else if (lv instanceof Code.MapLVal) {
					ofs = cod.operands[iidx];
					iidx += 1;
					if (idx > 1) {
						lin = "Xb = wyil_index_of(Xc, X" + ofs + ");" + tag;
						backFix = "wycc_map_add(Xc, X" + ofs + ", Xa);" + tag;
					} else {
						lin = "wycc_map_add(Xc, X" + ofs + ", X" + rhs + ");";
					}
					tmp = indent + lin + tag;
					tmp += " /* f */ ";
					this.mbodyAddLineNL(tmp);
					
				} else if(lv instanceof Code.RecordLVal) {
					Code.RecordLVal l = (Code.RecordLVal) lv;
					Type.EffectiveRecord tipe = l.rawType();
					tnam2 = flds.get(fidx);
					if (tipe instanceof Type.Record) {
						ofs = getFieldNames((Record) tipe).indexOf(tnam2);
						if (idx > 1) {
							lin = "Xb = wycc_record_get_dr(Xc, " + ofs + ");" + tag;
							backFix = "wycc_record_fill(Xc, " + ofs + ", Xa);" + tag;
						} else {
							lin = "wycc_record_fill(Xc, " + ofs + ", X" + rhs + ");";
						}
						tmp = indent + lin + tag;
						tmp += " /* g */ ";
						this.mbodyAddLineNL(tmp);
					} else {
						if (idx > 1) {
							lin = "Xb = wycc_record_get_nam(Xc, \"" + tnam2 + "\");" + tag;
							backFix = "wycc_record_put_nam(Xc, \"" + tnam2 + "\", Xa);" + tag;
						} else {
							lin = "wycc_record_put_nam(Xc, \"" + tnam2 + "\", X" + rhs + ");";
						}
						tmp = indent + lin + tag;
						tmp += " /* h */ ";
						this.mbodyAddLineNL(tmp);
					}
					fidx += 1;
				} else {
					error += "ERROR cannot yet do updates for type " + lv + "\n";
					return;
				}				
				idx -= 1;
			}
			return;
		}
		
		public void writeCodeSubList(Code codIn){
			String tmp;
			int targ, src, lhs, rhs, cnt;
		
			//bodyAddLineNL(	"// HELP needed for SubList"	);
			Code.SubList cod = (Code.SubList) codIn;
			targ = cod.target;
			cnt = cod.operands.length;
			if (cnt != 3) {
				error += "SubList operand count is "+ cnt + "\n";
				bodyAddLineNL(	"// HELP needed for  SubList"	);
				return;
				
			};
			src = cod.operands[0];
			lhs = cod.operands[1];
			rhs = cod.operands[2];
			cnt = -1;
			for (int itm : cod.operands) {
				if (itm == targ) {
					cnt = targ;
				}
			}
			tmp = " = "+ "wyil_list_sub(X" + src + ", X" + lhs + ", X" + rhs + ");";
			writeTargetSwap(tmp, targ, cnt);
			return;
		}
		
		public void writeCodeBinSetOp(Code codIn){
			String tmp;
			int targ, lhs, rhs, swp;
			String rtn, lin;

			//bodyAddLineNL(	"// HELP needed for BinSetOp"	);
			Code.BinSetOp cod = (Code.BinSetOp) codIn;
			Code.BinSetKind opr = cod.kind;
			targ = cod.target;
			lhs = cod.leftOperand;
			rhs = cod.rightOperand;
			
			if (opr == Code.BinSetKind.DIFFERENCE) {
				rtn = "wyil_set_diff";
			} else if (opr == Code.BinSetKind.INTERSECTION){
				rtn = "wyil_set_insect";
			} else if (opr == Code.BinSetKind.LEFT_DIFFERENCE){
				rtn = "wyil_set_diff_odd";
			} else if (opr == Code.BinSetKind.LEFT_INTERSECTION){
				rtn = "wyil_set_insect_odd";
			} else if (opr == Code.BinSetKind.LEFT_UNION){
				rtn = "wyil_set_union_odd";
			} else if (opr == Code.BinSetKind.RIGHT_INTERSECTION){
				rtn = "wyil_set_insect_odd";
				swp = lhs;
				lhs = rhs;
				rhs = swp;
			} else if (opr == Code.BinSetKind.RIGHT_UNION){
				rtn = "wyil_set_union_odd";
				swp = lhs;
				lhs = rhs;
				rhs = swp;
			} else if (opr == Code.BinSetKind.UNION){
				rtn = "wyil_set_union";
			} else {
				error += "BinSetOp un-defined\n";
				bodyAddLineNL(	"// HELP! needed for binSetOp '" + opr + "'"	);
				return;
			}
			tmp = " = "+ rtn + "(X" + lhs + ", X" + rhs + ");";
			writeTargetSwap(tmp, targ, lhs);

			return;
		}

		public void writeCodeBinListOp(Code codIn){
			int targ, lhs, rhs;
			String rtn;

			//bodyAddLineNL(	"// HELP needed for BinListOp"	);
			Code.BinListOp cod = (Code.BinListOp) codIn;
			Code.BinListKind opr = cod.kind;
			targ = cod.target;
			writeClearTarget(targ);
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
				bodyAddLineNL(	"// HELP! needed for binListOp '" + opr	);
				return;
			}
			this.mbodyAddLineTINL(	"X" + targ + " = " + rtn + "(X" + lhs + ", X" + rhs + ");"	);
			return;
		}

		//
		// do a lookup given a key (in a map) or an int (in a list)
		public void writeCodeIndexOf(Code codIn){
			int targ, lhs, rhs;
			
			Code.IndexOf cod = (Code.IndexOf) codIn;
			targ = cod.target;
			lhs = cod.leftOperand;
			rhs = cod.rightOperand;
			
			writeClearTarget(targ);
			this.addDecl(targ, "wycc_obj*");
			this.mbodyAddLineTINL(	"X" + targ + " = wyil_index_of(X" + lhs + ", X" + rhs + ");"	);
			return;
		}
		
		public void writeCodeLengthOf(Code codIn){
			int targ, rhs;
			String lin;
			
			Code.LengthOf cod = (Code.LengthOf) codIn;
			targ = cod.target;
			rhs = cod.operand;
			
			lin = " = wyil_length_of(X" + rhs + ");";
			writeTargetSwap(lin, targ, rhs);
			
			return;
		}

		// given an assignment (sans target), a target register, a source register, and a tag
		// handle declaring the target register, and producing the completed assignment
		// where an intermediate register has to be used if the target is the same as the source.
		private void writeTargetSwap(String lin, int targ, int opr){
			String t2;
			//String tag = this.sourceTag;
			
			this.addDecl(targ, "wycc_obj*");
			if (targ == opr) {
				if (targ != 0) {
					this.addDecl(-targ, "wycc_obj*");
					t2 = "XN" + targ;
				} else {
					t2 = "Xc";
				}
				this.mbodyAddLineTINL(	t2 + lin	);
				writeClearTarget(targ);
				this.addDecl(targ, "wycc_obj*");
				this.mbodyAddLineTINL(	"X" + targ + " = " + t2 + ";"	);
			} else {
				writeClearTarget(targ);
				this.mbodyAddLineTINL(	"X" + targ + lin	);
			}
			return;
		}
		
		public void writeCodeReturn(Code codIn){
			String lin;
			Integer k;
			String nam = "";
			Integer skip;
			int tgt;

			Code.Return cod = (Code.Return) codIn;
			
			if (returnSeen) {
				// seems like nothing else is needed here
			} else {
				this.mbodyAddLineNL("return0:");
			}
			returnSeen = true;
			if (retType instanceof Type.Void) {
				lin = "return;";
				tgt = 0;
			} else  {
				tgt = cod.operand;
				lin = "return X" + tgt + ";";
			}
			skip = params.size();
			for (Map.Entry<Integer, String> e : declsT.entrySet()) {
				k = e.getKey();
				if (k < skip) {
					continue;
				} if (tgt == k) {
					continue;
				} else {
					nam = " X" + k;
				}
				this.mbodyAddLineINL(	nam + " = wycc_deref_box(" + nam + ", 1);"	);
			}
			
			// **** may need to consider other return types
			this.mbodyAddLineTINL(	lin	);
			return;
		}

		public void writeCodeNewMap(Code codIn){
			int targ;
			String lin;
			boolean flg;

			//bodyAddLine(	"// HELP needed for NewMap"	);		
			Code.NewMap cod = (Code.NewMap) codIn;
			targ = cod.target;
			writeClearTarget(targ);
			this.addDecl(targ, "wycc_obj*");
			this.mbodyAddLineTINL(	"X" + targ + " = wycc_map_new(" + wyccTypeNone + ");"	);
			flg = false;
			lin = "";
			for (int itm : cod.operands) {
				if (flg) {
					lin += ", X" + itm + ");";
					this.mbodyAddLineTINL(	lin	);
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
			return;
		}
				
		public void writeCodeNewSet(Code codIn){
			int targ;

			//bodyAddLineNL(	"// HELP needed for NewSet"	);		
			Code.NewSet cod = (Code.NewSet) codIn;
			targ = cod.target;

			writeClearTarget(targ);
			this.addDecl(targ, "wycc_obj*");
			this.mbodyAddLineTINL(	"X" + targ + " = wycc_set_new(" + wyccTypeNone + ");"	);
			for (int itm : cod.operands) {
				this.mbodyAddLineTINL(	"wycc_set_add(X" + targ + ", X" + itm + ");"	);
			}
			return;
		}
		
		public void writeCodeNewList(Code codIn){
			int cnt;
			int targ;

			//bodyAddLineNL(	"// HELP needed for NewList"	);		
			Code.NewList cod = (Code.NewList) codIn;
			targ = cod.target;
			cnt = cod.operands.length;
			writeClearTarget(targ);
			this.addDecl(targ, "wycc_obj*");
			this.mbodyAddLineTINL(	"X" + targ + " = wycc_list_new(" + cnt + ");"	);
			for (int itm : cod.operands) {
				this.mbodyAddLineTINL(	"wycc_list_add(X" + targ + ", X" + itm + ");"	);
			}

			return;
		}

		public void writeCodeBinStringOp(Code codIn){
			String rtn;
			int targ, lhs, rhs;
			
			Code.BinStringOp cods = (Code.BinStringOp) codIn;
			//Code.BinStringKind opr = cods.kind;

			rtn = "wyil_strappend";
			targ = cods.target;
			lhs = cods.leftOperand;
			rhs = cods.rightOperand;
			writeClearTarget(targ);
			this.mbodyAddLineTINL(	"X" + targ + " = " + rtn + "(X" + lhs + ", X" + rhs + ");"	);
			this.addDecl(targ, "wycc_obj*");
			
			return;
		}
		
		public void writeCodeAssign(Code codIn){
			int targ, rhs;

			Code.Assign cod = (Code.Assign) codIn;
			targ = cod.target;
			rhs = cod.operand;
			if (targ == rhs) {
				bodyAddLineNL(	"//            Safely ignoring assign operation"	);
				return;
			}
			writeClearTarget(targ);
			// **** should check that types match
			this.addDecl(targ, "wycc_obj*");
			this.mbodyAddLineTINL(	"X" + targ + " = X" + rhs + ";"	);
			this.mbodyAddLineTINL(	"WY_OBJ_BUMP(X" + rhs + ");"	);
			return;
		}
			
		public void writeCodeConvert(Code codIn){
			String tmp;
			String dtyp;
			int tgt, opr;
			Type ntyp, otyp;

			Code.Convert cod = (Code.Convert) codIn;
			tgt = cod.target;
			opr = cod.operand;
			ntyp = cod.result;
			otyp = cod.type;
			if (ntyp instanceof Type.Any) {
				bodyAddLineNL(	"//            Safely ignoring convert operation to Any"	);
				return;
			}
			
			bodyAddLineNL(	"//**          change X" + opr + " from " + otyp + " to " + ntyp	);
			dtyp = writeDenseType(ntyp);
			bodyAddLineNL(	"//**          convert_parse " + dtyp	);
			tmp = "";
			if (ntyp instanceof Type.Union) {
				if (((Type.Union) ntyp).bounds().contains(otyp)) {
					bodyAddLineNL(	"//            Safely ignoring convert operation to supertype"	);
					return;					
				}
			}
			if (otyp instanceof Type.Leaf) {
				tmp += "was " + otyp + " ";
			}
			if (ntyp instanceof Type.Leaf) {
				tmp += "will be " + ntyp + " ";
			}
			if (tmp != "") {
				bodyAddLineNL(	"//--          " + tmp + " a leaf type"	);
			}
			this.addDecl(tgt, "wycc_obj*");			
			this.mbodyAddLineINL(	 "Xa = wyil_convert(X" + opr + ", \"" + dtyp + "\");"		);
			writeClearTarget(tgt);
			this.mbodyAddLineINL(	"X" + tgt + " = Xa;"		);
			return;
		}

		public void writeCodeBinArithOp(Code codIn){
			int targ, lhs, rhs;
			String rtn;
			
			Code.BinArithOp cod = (Code.BinArithOp) codIn;
			Code.BinArithKind opr = cod.kind;
			targ = cod.target;
			writeClearTarget(targ);
			this.addDecl(targ, "wycc_obj*");
			lhs = cod.leftOperand;
			rhs = cod.rightOperand;

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
				bodyAddLineNL(	"// HELP! needed for binArithOp '" + opr + "'"	);
				return;
			}
			this.mbodyAddLineTINL(	"X" + targ + " = " + rtn + "(X" + lhs + ", X" + rhs + ");"	);
			return;
		}
		
		public void writeCodeConstant(Code codIn){
			String tmp;
			int targ;
			Constant val;
			String nam;
			
			Code.Const cod = (Const) codIn;
			targ = cod.target;
			val = cod.constant;
			bodyAddLineNL(	"//             target " + targ	);
			nam = "X" + targ;

			tmp = this.writeMyConstant(val, nam, 0);
			if (tmp == null) {
				return;
			}
			this.addDecl(targ, "wycc_obj*");
			writeClearTarget(targ);			
			this.mbodyAddLine(tmp);
			return;
		}

		private String writeMyConstant(Constant val, String nam, int deep){
			return writeMyConstant(val, nam, this.sourceTag, deep);
		}
		
		private String writeMyConstant(Constant val, String nam, String tag, int deep){
			//final Integer intMax = (Integer) 2147483647;
			final BigInteger bigIntMax = new BigInteger("2147483647");
			final Constant.Integer wyIntMax = Constant.V_INTEGER(bigIntMax);
			String ans = "";
			String tmp;
			Type typ;
			String rval;
			String assn = null;
			int alt;
			int cnt;
			int tok;
			int idx;
			String anam, bnam;
			String dtyp;
			
			if ((deep % 2) == 1) {
				anam = "Xb";
				bnam = "Xa";
			} else {
				anam = "Xa";
				bnam = "Xb";
			}
			typ = val.type();
			rval = val.toString();
			if (typ instanceof Type.Strung) {	
				assn = "wycc_box_cstr(\"" + safeString1(rval) + "\")";
				Constant.Strung sq = (Constant.Strung) val;
				
				tmp = sq.value;
				idx =  tmp.indexOf('\n');
				bodyAddLineNL(	"//		Constant.Strung index is " + idx		);
			} else if (typ instanceof Type.Int) {
				//if (rval.length() > 9) {
				if (val.compareTo(wyIntMax) >= 0) {
					assn = "wycc_box_wint(\"" + rval + "\")";					
				} else {
					assn = "wycc_box_int(" + rval + ")";
				}
			} else if (typ instanceof Type.Bool) {
				if (rval.equals("true")) {
					rval = "1";
				} else {
					rval = "0";
				}
				assn = "wycc_box_bool(" + rval + ")";
			} else if (typ instanceof Type.Char) {
				assn = "wycc_box_char(" + rval + ")";
			} else if (typ instanceof Type.Byte) {
				alt = intFromByte(rval);
				assn = "wycc_box_byte(" + alt + ")";
			} else if (typ instanceof Type.Set) {
				assn = "wycc_set_new(-1)";
				Constant.Set cs = (Constant.Set) val;
				cnt = cs.values.size();
				bodyAddLineNL(	"//             with " + cnt + " initialisers"	);
				tmp = indent + nam + " = " + assn + ";" + tag + "\n";
				ans += tmp;
				tmp = indent + "{\n";
				ans += tmp;
				tmp = indent + indent + "wycc_obj *" + anam + " = (wycc_obj*)0;\n";
				ans += tmp;
				for (Constant itm:cs.values) {
					tmp = writeMyConstant(itm, anam, "", deep+1);
					ans += indent + tmp;
					tmp = indent + indent + "wycc_set_add(" + nam + ", " + anam + ");\n";
					ans += tmp;
					tmp = indent + indent + "wycc_deref_box(" + anam + ", 0);\n";
					ans += tmp;
				}
				tmp = indent + "}\n";
				ans += tmp;
				return ans;
			} else if (typ instanceof Type.Map) {
				// **** does this need to handle initial values, as set does
				bodyAddLineNL(	"// HELP! needed in const for value type '" + typ + "'"	);
				assn = "wycc_map_new(-1)";
				Constant.Map cm = (Constant.Map)val;
			} else if (typ instanceof Type.Record) {
				Constant.Record cr = (Constant.Record) val;
				cnt = cr.values.size();
				tmp = "//             with " + cnt + " initialisers";
				bodyAddLineNL(tmp);
				tok = registerRecordType((Record) typ);
				assn = "wycc_record_new(record_reg[" + tok + "])";
				tmp = indent + nam + " = " + assn + ";" + tag + "\n";
				ans += tmp;
				tmp = indent + "{\n";
				ans += tmp;
				tmp = indent + indent + "wycc_obj *" + anam + " = (wycc_obj*)0;\n";
				ans += tmp;
				idx = 0;
				for (String ke:getFieldNames((Record) typ)){
					tmp = writeMyConstant(cr.values.get(ke), anam, "", deep+1);
					ans += indent + tmp;
					tmp = indent + indent + "wycc_record_fill(" + nam + ", " + idx +", " + anam + ");\n";
					ans += tmp;
					tmp = indent + indent + "wycc_deref_box(" + anam + ", 0);\n";
					ans += tmp;
					idx += 1;
				}
				tmp = indent + "}\n";
				ans += tmp;
				return ans;
			} else if (typ instanceof Type.List) {
				Constant.List cl = (Constant.List) val;
				cnt = cl.values.size();
				tmp = "//             with " + cnt + " initialisers";
				bodyAddLineNL(tmp);
				assn = "wycc_list_new(" + cnt + ")";
				tmp = indent + nam + " = " + assn + ";" + tag + "\n";
				ans += tmp;
				tmp = indent + "{\n";
				ans += tmp;
				tmp = indent + indent + "wycc_obj *" + anam + " = (wycc_obj*)0;\n";
				ans += tmp;
				for (Constant itm:cl.values) {
					tmp = writeMyConstant(itm, anam, "", deep+1);
					ans += indent + tmp;
					tmp = indent + indent + "wycc_list_add(" + nam + ", " + anam + ");\n";
					ans += tmp;
					tmp = indent + indent + "wycc_deref_box(" + anam + ", 0);\n";
					ans += tmp;
				}
				tmp = indent + "}\n";
				ans += tmp;
				return ans;
			} else if (typ instanceof Type.Tuple) {
				Constant.Tuple ct = (Constant.Tuple) val;
				cnt = ct.values.size();
				tmp = "//             with " + cnt + " initialisers";
				bodyAddLineNL(tmp);
				assn = "wycc_tuple_new(" + cnt + ")";
				tmp = indent + nam + " = " + assn + ";" + tag + "\n";
				ans += tmp;
				tmp = indent + "{\n";
				ans += tmp;
				tmp = indent + indent + "wycc_obj *" + anam + " = (wycc_obj*)0;\n";
				ans += tmp;
				idx = 0;
				for (Constant itm:ct.values) {
					tmp = writeMyConstant(itm, anam, "", deep+1);
					ans += indent + tmp;
					tmp = indent + indent + "wycc_update_list(" + nam + ", " + anam + "," + idx + ");\n";
					ans += tmp;
					tmp = indent + indent + "wycc_deref_box(Xc, 0);\n";
					ans += tmp;
					idx += 1;
				}
				tmp = indent + "}\n";
				ans += tmp;
				return ans;
				
			} else if (typ instanceof Type.Null) {
				assn = "wycc_box_null()";
			} else if (typ instanceof Type.Real) {
				if (floatFlag) {
					assn = "wycc_box_float((long double)" + rval + ")";
				} else {
					assn = "wycc_box_ratio(\"" + rval + "\")";
				}
			} else if (typ instanceof Type.FunctionOrMethod) {
				Constant.Lambda fom = (Constant.Lambda) val;
				bodyAddLineNL(	"// HELP! needed in const for FOM name: '" + fom.name.name() + "'"	);
				dtyp = writeDenseType(typ);
				bodyAddLineNL(	"// HELP! needed in const for FOM dtyp: '" + dtyp + "'"	);
				tok = lookupNumFOMname(fom.name.name(), typ);
				assn = "fom_handle_reg[" + tok + "]";			
			} else {
				bodyAddLineNL(	"// HELP! needed in const for value type '" + typ + "'"	);
				return null;
			}
			//this.addDecl(targ, "wycc_obj*");
			//writeClearTarget(targ);
			// assn += "(" + rval + ")";
			// this.addDecl(targ, "wycc_obj*");
			//if (assn != null) {
				tmp = indent + nam + " = " + assn + ";" + tag + "\n";
				//this.mbodyAddLine(tmp);
			//} else {
				//tmp = "";
			//}
			return tmp;
		}
		
		private int intFromByte(String txt){
			int ans = 0;
			int idx = 0;
			char chr;
			
			while (idx<8){
				chr = txt.charAt(idx);
				if (chr == 'b') {
					return ans;
				}
				ans *= 2;
				if (chr == '1') {
					ans += 1;
				}
				
				idx += 1;
			}
			return ans;
		}
		
		// A register is about to be clobbered; dereference any object.
		// negative register numbers are our own constructs, very local, not ref counted.
		public void writeClearTarget(int target){
			String nam = "";
			Integer tgt = target;


			if (declsU.contains(tgt)) {
				if (target < 0) {
					//nam = "XN" + (-target);
				} else {
					nam = "X" + target;
					this.mbodyAddLineTINL(	nam + " = wycc_deref_box(" + nam + ", 0);"	);
				}
			}
			declsU.add(tgt);
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
	
	private String writeDenseType(Type ntyp) {
		
		if (ntyp instanceof Type.Void) {
			return "v";
		}
		if (ntyp instanceof Type.Compound) {
			Type.Compound compound = (Type.Compound) ntyp;
			Automaton automaton = compound.automaton;

			return writeTypeCompound(automaton);
		}
		if (ntyp instanceof Type.Nominal) {
			return "N";							// **** need to fix; had nid
		}
		if (ntyp instanceof Type.Strung) {
			return "s";
		}
		if (ntyp instanceof Type.Real) {
			return "r";
		}
		if (ntyp instanceof Type.Int) {
			return "i";
		}
		if (ntyp instanceof Type.Byte) {
			return "d";
		}
		if (ntyp instanceof Type.Char) {
			return "c";
		}
		if (ntyp instanceof Type.Bool) {
			return "b";
		}
		if (ntyp instanceof Type.Meta) {
			return "m";
		}
		if (ntyp instanceof Type.Void) {
			return "v";
		}
		if (ntyp instanceof Type.Any) {
			return "a";
		}
		if (ntyp instanceof Type.Null) {
			return "n";
		}
		
		return "&";
	}
	private String writeTypeCompound(Automaton automaton) {

		BitSet headers = new BitSet(automaton.size());
		BitSet visited = new BitSet(automaton.size());
		BitSet onStack = new BitSet(automaton.size());
		findHeaders(0, visited, onStack, headers, automaton);
		visited.clear();
		String[] titles = new String[automaton.size()];
		int count = 0;
		for (int i = 0; i != automaton.size(); ++i) {
			if (headers.get(i)) {
				titles[i] = headerTitle(count++);
			}
		}
		return writeTypeCompound(0, visited, titles, automaton);
	}
	
	private void findHeaders(int index, BitSet visited,
				BitSet onStack, BitSet headers, Automaton automaton) {
		if(visited.get(index)) {
				// node already visited
			if(onStack.get(index)) {
				headers.set(index);
			}
			return; 
		} 		
		onStack.set(index);
		visited.set(index);
		State state = automaton.states[index];
		for(int child : state.children) {
			findHeaders(child,visited,onStack,headers,automaton);
		}	
		onStack.set(index,false);
	}
	private char[] headers = { 'X','Y','Z','U','V','W','L','M','N','O','P','Q','R','S','T'};
	private String headerTitle(int count) {
		String r = Character.toString(headers[count%headers.length]);
		int n = count / headers.length;
		if(n > 0) {
			return r + n;
		} else {
			return r;
		}
	}	
	
	private String writeTypeCompound(int index, BitSet visited,
			String[] headers, Automaton automaton) {
		String sep = "";
		String middle;

		if (visited.get(index)) {
			// node already visited
			return headers[index];
		} else if(headers[index] != null) {
			visited.set(index);
		}
		String header = headers[index];
		State state = automaton.states[index];

		switch (state.kind) {
		case Type.K_VOID:
			return "v";
		case Type.K_ANY:
			return "a";
		case Type.K_NULL:
			return "n";
		case Type.K_BOOL:
			return "b";
		case Type.K_BYTE:
			return "d";
		case Type.K_CHAR:
			return "c";
		case Type.K_INT:
			return "i";
		case Type.K_RATIONAL:
			return "r";
		case Type.K_STRING:
			return "s";
		case Type.K_NOMINAL:
			System.err.println("milestone 5.3.C1+");
			return "[$]";
		};
		int[] children = state.children;
		
		switch (state.kind) {
		case Type.K_SET: {
			middle = "[*";
			boolean nonEmpty = (Boolean) state.data;
			if (nonEmpty) {
				middle +=  "+";
			}
			// *** need to check that there is exactly 1 child
			break;
		}
		case Type.K_LIST: {
			middle = "[#";
			boolean nonEmpty = (Boolean) state.data;
			if (nonEmpty) {
				middle +=  "+";
			}
			// *** need to check that there is exactly 1 child
			break;
		}
		case Type.K_REFERENCE:
			middle = "[.";
			// *** need to check that there is exactly 1 child
			break;
		case Type.K_NEGATION: {
			middle = "[!";
			// *** need to check that there is exactly 1 child
			break;
		}
		case Type.K_MAP: {
			// binary node
			middle = "[@";
			// *** need to check that there are exactly 2 children
			break;
		}		
		case Type.K_UNION: {
			middle = "[|";
			break;
		}
		case Type.K_TUPLE: {
			middle = "[=";
			break;
		}
		case Type.K_RECORD: {
			// labeled nary node
			Record.State rs = (Record.State) state.data;
			if (rs.isOpen) {
				middle = "[%";
			}else {
				middle = "[{";
			}
			//middle = "[{";
			middle += writeTypeFieldNames(rs);
			middle +=  "}";
			break;
		}		
		case Type.K_METHOD: {
			middle = "[:";
			break;
		}
		case Type.K_FUNCTION: {
			middle = "[^";
			break;
		}		
		default: 
			throw new IllegalArgumentException("Invalid type encountered (kind: " + state.kind +")");
		}
		sep = "";		
		for (int i = 0; i != children.length; ++i) {
			middle+= sep;
			middle += writeTypeCompound(children[i], visited, headers, automaton);
			sep = ",";
		}
		
		middle +=  "]";			
		// Finally, check whether this is a header node, or not. If it is a
		// header then we need to insert the recursive type.

		if(header != null) {
			// The following case is interesting. Basically, we'll never revisit
			// a header. Therefore, if we have multiple edges landing on a
			// header we must update the header string to represent the full
			// type reachable from the header.
			//String r = header + "<" + middle + ">"; 
			String r = "[<" + header + "}" + middle + "]"; 
			//headers[index] = r;
			return r;
		} 
		return middle;

	}

	private String writeTypeFieldNames(wyil.lang.Type.Record.State fields) {
		String ans= "";
		String sep = "";
		
		for (int i = 0; i != fields.size(); ++i) {
			ans += sep;
			ans += fields.get(i);
			sep = ",";
		}
		return ans;
	}

	public ArrayList<String> getFieldNames(Type.Record record) {
	    ArrayList<String> fields = new ArrayList<String>(record.keys());
	    Collections.sort(fields);
	    return fields;
	}
	
	public String safeString2(String was){
		return safeString(was, 0, was.length(), false);
	}
	
	public String safeString1(String was){
		return safeString(was, 1, was.length()-1, true);
	}

	public String safeString(String was){
		return safeString(was, 0, was.length(), true);
	}
	public String safeString(String was, int lo, int hi, boolean flg){	
		String ans = "";
		int at = lo;
		char ch;
		
		while (at < hi) {
			ch = was.charAt(at);
			if (ch == '\\') {
				ans += "\\\\";
			} else if (ch == '\n') {
				ans += "\\n";
			} else if ((ch == '"') && flg) {
				ans += "\\\"";
			} else {
				ans += ch;
			}
			at++;
		}
		
		return ans;
	}
	
}
