package wycc.io;

import java.io.*;
import java.util.*;
//import java.util.ArrayList;
//import java.util.Collection;

import wyil.lang.Attribute;
import wyil.lang.Block;
import wyil.lang.Code;
import wyil.lang.Code.Const;
import wyil.lang.Modifier;
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
		
		output.println("// WYIL Module: " + module.id());
		output.println("// WYIL Filename: " + module.filename());
		output.println("#line 0 \"" + module.id() + ".whiley\"");
		output.println(this.includeFile);
		
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
			this.writeMethod(md, cnt);
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
	
	public void writeMethod(MethodDeclaration metDe, int idx) {
		int cnt;
		String lin;
		String nam;
		String[] ans = new String[2];
		ans[0] = "";
		ans[1] = "";
		
		nam = metDe.name();
		List<Modifier> mods = metDe.modifiers();
		List<Case> cas = metDe.cases();
		List<Attribute> atts = metDe.attributes();
		
		 lin = "#" + idx + " (";
		lin += atts.size();
		lin += ":";
		lin += mods.size();
		lin += ":";
		lin += cas.size();
		lin +=") is named " + nam;
		output.println("// WYIL method declaration " + lin);

		//output.println("//    is called: " + metDe.name());
		if (metDe.isMethod()) {
			output.println("//   is method.");
		}
		if (metDe.isFunction()) {
			output.println("//   is function.");
		}
		// output.println("// WYIL count of modifiers: " + mods.size());
		 cnt = 0;
		for (Modifier mo : mods) {
			cnt += 1;
			this.writeModifier(mo, cnt);
		}
		//output.println("// WYIL count of cases: " + cas.size());
		 cnt = 0;
		for (Case ci : cas) {
			cnt += 1;
			this.writeCase(ci, cnt, ans);
		}
		lin = "void " + this.mungName(nam) + "() {";
		output.println(lin);
		output.println(ans[0]);
		output.println(ans[1]);
		output.println("}");

	}
	
	public void writeModifier(Modifier mod, int idx) {
		output.println("// **** Need help with modifier #" + idx);
	}	
	
	public void writeCase(Case casIn, int idx, String[] ans) {
		int cnt = -1;
		String decl = ans[0];
		String inst = ans[1];
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
		 this.writeBody(bod, idx, ans);
		 
		 // ans[0] = decl;
		 
	}
	
	public void writeBody(Block bodIn, int idx, String[] ans) {
		int cnt = -1;

		if (bodIn == null) {
			output.println("// block #" + idx + " is null ");
			return;
		}
		cnt = bodIn.size();
		output.println("// block #" + idx + " is of seizes " + cnt);
		 cnt = 0;
		for (Block.Entry be: bodIn) {
			this.writeBlockEntry(be, cnt, ans);
			cnt += 1;
		}
		
	}

	public void writeBlockEntry(Block.Entry blkIn, int idx, String[] ans) {
		int targ;
		Value val;
		String tyc;
		String lin;
		String ctyp = "void";
		String assn = "";
		String decl = "";
		String inst = "";
		
		output.println("// block.entry #" + idx);
		
		Code cod = blkIn.code;
		String temp = cod.toString();
		output.println("//             Looks like " + temp);
		String[] frags = temp.split(" ", 4);
		String opc = frags[0];
		// output.println("//             Looks like '" + opc + "'");
		
		if(cod instanceof Code.Const) {

			Code.Const codc = (Const) cod;
			targ = codc.target;
			val = codc.constant;
			// output.println("//             as a const w" + frags[1]);
			output.println("//             target "+ targ);
			tyc = val.type().toString();
			if (tyc.equals("string")){
				// ctyp = "char* ";
				// assn = " = " + val  ;
				ctyp = "wycc_obj* ";
				assn = " = wycc_box_str(" + val + ")" ;
			} else {
				output.println("// HELP needed for value type '" + tyc + "'");
			}
			lin = "	" + ctyp + " X" + targ + assn + ";";
			output.println("// line looks like '" + lin + "'");
			decl += lin + "\n";
		} else if (cod instanceof Code.Debug){
			Code.Debug codd = (Code.Debug) cod;
			targ = codd.operand;
			//lin = "	wyil_debug_str(X" + targ + ");" ;
			lin = "	wyil_debug_obj(X" + targ + ");" ;
			inst += lin + "\n";
		} else if (cod instanceof Code.Return){
			lin = "	return;" ;
			inst += lin + "\n";
		} else if (cod instanceof Code.StringOp){
			output.println("// HELP needed 4 opcode '" + opc + "'");
		} else {
			output.println("// HELP needed for opcode '" + opc + "'");
		}
		ans[0] += decl;
		ans[1] += inst;
	}
	
	private String mungName(String nam){
		String ans = this.getManglePrefix();
		ans += nam;
		return ans;
	}
	
}

