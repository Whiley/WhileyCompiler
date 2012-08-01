package wycc.io;

import java.io.*;
import java.util.*;
//import java.util.ArrayList;
//import java.util.Collection;

import wybs.lang.Path;
import wyil.lang.Attribute;
import wyil.lang.Block;
import wyil.lang.Code;
import wyil.lang.Code.Const;
import wyil.lang.Code.StringOperation;
import wyil.lang.Modifier;
import wyil.lang.NameID;
import wyil.lang.Type;
import wyil.lang.Value;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Case;
import wyil.lang.WyilFile.ConstantDeclaration;
import wyil.lang.WyilFile.MethodDeclaration;
import wyil.lang.WyilFile.TypeDeclaration;

public class CFileWriter {
	private final PrintStream output;
	private final String defaultManglePrefix = "wycc_";
	private final String includeFile = "#include \"lib/wycc_lib.h\"";
	private String manglePrefix = null;
	private int initor_flg = 1;
	private String name;
	
	public CFileWriter(OutputStream output) {
		this.output = new PrintStream(output);
		this.output.println("// WYIL to C ");
	}
	
	public void setManglePrefix(String str){
		this.manglePrefix = str;
	}
	
	public String getManglePrefix(){
		if (this.manglePrefix == null) {
			this.manglePrefix = this.defaultManglePrefix;
		}
		return this.manglePrefix;
	}
	
	public void write(WyilFile module) {
		int cnt;
		List<Method> mets = new ArrayList<Method>();
		
		this.name = module.id().toString();
		this.writePreamble(module);
		
		Collection<TypeDeclaration> typCol = module.types();
		Collection<ConstantDeclaration> conCol = module.constants();
		Collection<MethodDeclaration> modCol = module.methods();
		output.println("// WYIL module count of types: " + typCol.size());
		output.println("// WYIL module count of constants: " + conCol.size());
		output.println("// WYIL module count of methods: " + modCol.size());

		 cnt = 0;
		for (TypeDeclaration td : typCol) {
			cnt += 1;
			this.writeType(td, cnt);
		}

		 cnt = 0;
		for (ConstantDeclaration cd : conCol) {
			cnt += 1;
			this.writeConstant(cd, cnt);
		}
		
		 cnt = 0;
		for (MethodDeclaration md : modCol) {
			cnt += 1;
			//this.writeMethod(md, cnt);
			Method met = new Method(md, cnt);
			mets.add(met);
		}
		
		for (Method met : mets){
			met.write();
		}
		this.writePostamble();
	}
	
	private void writePreamble(WyilFile module){
		output.println("#line 0 \"" + module.id() + ".whiley\"");
		output.println("// WYIL Module: " + name);
		output.println("// WYIL Filename: " + module.filename());
		
		output.println(this.includeFile);

	}
	
	private void writePostamble(){
		if (this.initor_flg != 0){
			output.println("static void __initor_b() {");
			output.println("	if (wycc_debug_flag != 0)");
			output.println("		wyil_debug_str(\"initialization for " + this.name + "\");");
			output.println("	return;");
			output.println("}");
			output.println("");
			output.println("static wycc_initor __initor_c;");
			output.println("__attribute__ ((constructor)) static void __initor_a(){");
			output.println("	__initor_c.nxt = wycc_init_chain;");
			output.println("	__initor_c.function = __initor_b;");
			output.println("	wycc_init_chain = &__initor_c;");
			output.println("	return;");
			output.println("}");
		}

	}

	public void writeType(TypeDeclaration typDe, int idx) {
		// int cnt;
		String lin;
		
		// output.println("// **** Need help with type declaration #" + idx);
		Block strain =  typDe.constraint();
		List<Modifier> mods = typDe.modifiers();
		Type typ = typDe.type();
		List<Attribute> atts = typDe.attributes();
		
		 lin = "#" + idx;
		lin += "(" + atts.size() + ":" + mods.size() + ")";
		lin +=" is named " + typDe.name();
		output.println("// WYIL type declaration " + lin);
		if (typDe.isProtected()) {
			output.println("//                 is Protected");
		}
		if (typDe.isPublic()) {
			output.println("//                 is Public");
		}
		if (strain != null) {
			output.println("//                 with constraints");
		}
		if (typ != null) {
			output.println("//                 with a type");
		}

	}
	
	public void writeConstant(ConstantDeclaration conDe, int idx) {
		output.println("// **** Need help with constant decalaration #" + idx);
	}	
	
	
	public class Method {
		private MethodDeclaration declaration;
		private int index;
		private String body;
		private String delt;		// the deconstructors
		private Map<Integer, String> decls;
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
		
		public Method(MethodDeclaration metDe, int idx){
			String lin;
			int cnt;
			declaration = metDe;
			index = idx;
			name = metDe.name();
			indent = "	";
			decls = new HashMap<Integer, String>();
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
			
			 lin = "#" + index + " (";
			lin += atts.size();
			lin += ":";
			lin += mods.size();
			lin += ":";
			lin += cas.size();
			 lin = "#" + index + " (";
			lin += atts.size();
			lin += ":";
			lin += mods.size();
			lin += ":";
			lin += cas.size();
			lin +=") is named " + name;
			output.println("// WYIL method declaration " + lin);
			if (declaration.isMethod()) {
				output.println("//   is method.");
			}
			if (declaration.isFunction()) {
				output.println("//   is function.");
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
		
		public void write() {
			int cnt;
			String lin;
		
			 cnt = 0;
			for (Case ci : cas) {
				cnt += 1;
				this.writeCase(ci, cnt);
			}
			if (error != "") {
				output.println("ERROR in " + name);
				output.println(error);
				return;
			}
			if (isNative) {
				return;
			}
			lin = "void " + mungName(name) + "() {";
			output.println(lin);
			output.println(writeDecls());
			output.println(body);
			output.println("}");

		}
		private String writeDecls(){
			String ans = "";
			//Integer idx;
			//for (lin : this.decls.values()){
			for (Map.Entry<Integer, String> e : decls.entrySet()){
				ans += indent + e.getValue() + "\n";
			}
			return ans;
		}

		private void addDecl(int target, String lin) {
			Integer tgt = target;
			String tst = decls.get(tgt);
			if (tst != null) {
				error += "multiple decalrations for X" + target + "\n";
			} else {
				decls.put(tgt, lin);
			}
			
		}
		public void checkModifier(Modifier mod, int idx) {
			String tag = "Unknown";
			// output.println("// **** Need help with modifier #" + idx);
			if(mod instanceof Modifier.Export) {
				tag = "Export";
				this.isExport = true;
			} else if(mod instanceof Modifier.Native) {
				tag = "Native";
				this.isNative = true;
			} else if(mod instanceof Modifier.Private) {
				tag = "Private";
				this.isPrivate = true;
			} else if(mod instanceof Modifier.Protected) {
				tag = "Protected";
				this.isProtected = true;
			} else if(mod instanceof Modifier.Public) {
				tag = "Public";
				this.isPublic = true;
			}
			output.println("// modifier #" + idx + " is " + tag);
		}
		
		public void checkCase(Case casIn, int idx) {
			int cnt = -1;
			List<Attribute> attCol = casIn.attributes();
			Block bod = casIn.body();
			Block prec = casIn.precondition();
			Block posc = casIn.postcondition();
			List<String> locals = casIn.locals();
		
			if (attCol == null) {
				output.println("//           " +  " no attributes");
			} else {
				cnt = attCol.size();
				output.println("//           " +  " with " + cnt + " attributes");
			}
			if (prec == null) {
				output.println("//           " +  " no precondition");
			} else {
				cnt = prec.size();
				output.println("//           " +  " precondition of size " + cnt);
			}
			if (posc == null) {
				output.println("//           " +  " no postcondition");
			} else {
				cnt = posc.size();
				output.println("//           " +  " postcondition of size " + cnt);
			}
			cnt = locals.size();
			output.println("//           " +  " with " + cnt + " locals");
			if (cnt < 1){
				return;
			}
			cnt = 1;
			for (String nam : locals) {
				output.println("//           " + cnt + " '" + nam + "'");
			}
		}
		
		public void writeCase(Case casIn, int idx) {
			int cnt = -1;
			//List<Attribute> attCol = casIn.attributes();
			Block bod = casIn.body();
			//Block prec = casIn.precondition();
			//Block posc = casIn.postcondition();
			//List<String> locals = casIn.locals();
		
			// output.println("//           " +  " with " + cnt + " locals");
			// if (! isNative) {
			this.writeBody(bod, idx);
			//}
		 
		}
	
		public void writeBody(Block bodIn, int idx) {
			int cnt = -1;

			if (bodIn == null) {
				output.println("// block #" + idx + " is null ");
				return;
			}
			cnt = bodIn.size();
			output.println("// block #" + idx + " is of seizes " + cnt);
			cnt = 0;
			for (Block.Entry be: bodIn) {
				this.writeBlockEntry(be, cnt);
				cnt += 1;
			}
		
		}

		//
		// convert a block entry code into some lines of C code to put in the file
		// **** changes needed
		// * tracking variables declared
		// * code for variable destruction
		// * even a subclass for routines
		//
		public void writeBlockEntry(Block.Entry blkIn, int idx) {
			int targ, lhs, rhs;
			Value val;
			String tyc;
			String lin;
			String ctyp = "void";
			String assn = "";
			String sep;
		
			output.println("// block.entry #" + idx);
		
			Code cod = blkIn.code;
			String temp = cod.toString();
			output.println("//             Looks like " + temp);
			String[] frags = temp.split(" ", 4);
			String opc = frags[0];
		
			if(cod instanceof Code.Const) {

				Code.Const codc = (Const) cod;
				targ = codc.target;
				val = codc.constant;
				output.println("//             target "+ targ);
				tyc = val.type().toString();
				if (tyc.equals("string")){
					ctyp = "wycc_obj* ";
					assn = " = wycc_box_str(" + val + ")" ;
				} else if (tyc.equals("int")){
					ctyp = "wycc_obj* ";
					assn = " = wycc_box_int(" + val + ")" ;
				} else {
					output.println("// HELP needed for value type '" + tyc + "'");
				}
				lin = ctyp + " X" + targ + assn + ";";
				this.addDecl(targ, lin);
			} else if (cod instanceof Code.Debug){
				Code.Debug codd = (Code.Debug) cod;
				targ = codd.operand;
				lin = "	wyil_debug_obj(X" + targ + ");" ;
				this.body += lin + "\n";
			} else if (cod instanceof Code.Return){
				lin = "	return;" ;
				this.body += lin + "\n";
			} else if (cod instanceof Code.StringOp){
				// output.println("// HELP needed 4 opcode '" + opc + "'");
				Code.StringOp cods = (Code.StringOp) cod;
				Code.StringOperation opr = cods.operation;
				//if (opr != Code.StringOperation APPEND){
				//	
				//}
				String rtn = "wyil_strappend";
				targ = cods.target;
				lhs = cods.leftOperand;
				rhs = cods.rightOperand;
				lin = "X" + targ + " = " + rtn + "(X" + lhs + ", X" + rhs + ");" ;
				this.body += "	" + lin + "\n";
				lin = "wycc_obj* X" + targ + ";";
				this.addDecl(targ, lin);
			} else if (cod instanceof Code.Assign){	
				Code.Assign coda = (Code.Assign) cod;
				targ = coda.target;
				rhs = coda.operand;
				// **** should check that types match
				lin = "X" + targ + " = X" + rhs + ";" ;
				this.body += "	" + lin + "\n";
			} else if (cod instanceof Code.Invoke){
				Code.Invoke codi = (Code.Invoke) cod;
				targ = codi.target;
				NameID nid = codi.name;
				Path.ID pat = nid.module();
				String nam = nid.name();
				output.println("// HELP for NameID '" + nam + "' with" + pat);
				lin = "X" + targ + " = " + nam + "(" ;
				sep = "";
				for (int itm : codi.operands) {
					lin += sep + "X" + itm;
					sep = ", ";
				}
				lin += ");";
				this.body += "	" + lin + "\n";
			} else {
				output.println("// HELP needed for opcode '" + opc + "'");
			}
		}
	}
	private String mungName(String nam){
		String ans = this.getManglePrefix();
		ans += nam;
		return ans;
	}
	
}

