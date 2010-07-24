package wyjc.lang;

import java.util.*;
import wyil.util.*;
import wyil.lang.*;

public class WhileyFile {
	public final ModuleID module;
	public final String filename;
	public final ArrayList<Decl> declarations;
	
	public WhileyFile(ModuleID module, String filename, List<Decl> decls) {
		this.module = module;
		this.filename = filename;
		this.declarations = new ArrayList<Decl>(decls);
	}
	
	public interface Decl extends SyntacticElement {
		public String name();
	}
	
	public static class ImportDecl extends SyntacticElement.Impl implements Decl {
		public ArrayList<String> pkg;
		
		public ImportDecl(List<String> pkg, Attribute... attributes) {
			super(attributes);
			this.pkg = new ArrayList<String>(pkg);
		}
		
		public ImportDecl(List<String> pkg, Collection<Attribute> attributes) {
			super(attributes);
			this.pkg = new ArrayList<String>(pkg);
		}
		
		public String name() {
			return "";
		}		
	}

	public static class ConstDecl extends
				SyntacticElement.Impl implements Decl {
		
		public final List<Modifier> modifiers;
		public final Expr value;
		public final String name;

		public ConstDecl(List<Modifier> modifiers, Expr value, String name,
				Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.value = value;
			this.name = name;
		}

		public ConstDecl(List<Modifier> modifiers, Expr value, String name,
				Collection<Attribute> attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.value = value;
			this.name = name;
		}

		public String name() {
			return name;
		}
		
		public boolean isPublic() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}
		
		public String toString() {
			return "define " + value + " as " + name;
		}
	}
	
	public static class TypeDecl extends SyntacticElement.Impl implements Decl {
		public final List<Modifier> modifiers;
		public final Type type;	
		public final Expr constraint;
		public final String name;

		public TypeDecl(List<Modifier> modifiers, Type type, String name, Expr constraint,
				Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.type = type;
			this.name = name;
			this.constraint = constraint;
		}

		public TypeDecl(List<Modifier> modifiers, Type type, String name, Expr constraint,
				Collection<Attribute> attributes) { 
			super(attributes);		
			this.modifiers = modifiers;
			this.type = type;			
			this.name = name;
			this.constraint = constraint;
		}				
		
		public boolean isPublic() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}
		
		public String name() { return name; }		
		
		public String toString() {
			return "define " + type + " as " + name;
		}
	}

	public final static class FunDecl extends SyntacticElement.Impl implements Decl {
		public final ArrayList<Modifier> modifiers; 
		public final String name;
		public final Receiver receiverType;
		public final Return returnType;
		public final ArrayList<Parameter> parameters;
		public final Expr constraint;		
		public final ArrayList<Stmt> statements;

		/**
		 * Construct an object representing a Whiley function.
		 * 
		 * @param name -
		 *            The name of the function.
		 * @param returnType -
		 *            The return type of this method
		 * @param paramTypes -
		 *            The list of parameter names and their types for this method
		 * @param constraint -
		 *            The constraint which must hold true on entry and exit (maybe null)
		 * @param statements -
		 *            The Statements making up the function body.
		 */
		public FunDecl(List<Modifier> modifiers, String name,
				Receiver receiverType, Return returnType,
				List<Parameter> parameters, Expr constraint,
				List<Stmt> statements, Collection<Attribute> attributes) {
			super(attributes);
			this.modifiers = new ArrayList<Modifier>(modifiers);
			this.name = name;		
			this.receiverType = receiverType;
			this.returnType = returnType;
			this.parameters = new ArrayList<Parameter>(parameters);
			this.constraint = constraint;			
			this.statements = new ArrayList<Stmt>(statements);
		}

		/**
		 * Construct an object representing a Whiley function.
		 * 
		 * @param name -
		 *            The name of the function.
		 * @param returnType -
		 *            The return type of this method
		 * @param paramTypes -
		 *            The list of parameter names and their types for this method
		 * @param constraint -
		 *            The constraint which must hold true on entry and exit (maybe null)
		 * @param statements -
		 *            The Statements making up the function body.
		 */
		public FunDecl(List<Modifier> modifiers, String name,
				Receiver receiverType, Return returnType,
				List<Parameter> parameters, Expr constraint,
				List<Stmt> statements, Attribute... attributes) {
			super(attributes);
			this.modifiers = new ArrayList<Modifier>(modifiers);
			this.name = name;		
			this.receiverType = receiverType;
			this.returnType = returnType;
			this.parameters = new ArrayList<Parameter>(parameters);
			this.constraint = constraint;			
			this.statements = new ArrayList<Stmt>(statements);
		}
		
		public boolean isPublic() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}
		
		public String name() {
			return name;
		}
	}
	
	public static final class Parameter extends SyntacticElement.Impl implements Decl {
		public final Type type;
		public final String name;

		public Parameter(Type type, String name, Attribute... attributes) {
			super(attributes);
			this.type = type;
			this.name = name;
		}

		public Parameter(Type type, String name, Collection<Attribute> attributes) {
			super(attributes);
			this.type = type;
			this.name = name;
		}
		
		public String name() {
			return name;
		}
	}
	
	public static final class Return extends SyntacticElement.Impl {
		public final Type type;

		public Return(Type type, Attribute... attributes) {
			super(attributes);
			this.type = type;			
		}

		public Return(Type type, Collection<Attribute> attributes) {
			super(attributes);
			this.type = type;			
		}		
	}

	public static final class Receiver extends SyntacticElement.Impl {
		public final Type type;

		public Receiver(Type type, Attribute... attributes) {
			super(attributes);
			this.type = type;			
		}

		public Receiver(Type type, Collection<Attribute> attributes) {
			super(attributes);
			this.type = type;			
		}	
	}
}
