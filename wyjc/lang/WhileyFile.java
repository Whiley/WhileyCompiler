package wyjc.lang;

import java.util.*;
import wyil.ModuleLoader;
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
	
	public ModuleLoader.Skeleton skeleton() {		
		return new ModuleLoader.Skeleton(module) {
			public boolean hasName(String name) {
				// FIXME: improve performance!
				for(Decl d : declarations) {
					if(d.name().equals(name)) {
						return true;
					}
				}
				return false;
			}			
		};
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
		public final Expr constant;
		public final String name;

		public ConstDecl(List<Modifier> modifiers, Expr constant, String name,
				Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.constant = constant;
			this.name = name;
		}

		public ConstDecl(List<Modifier> modifiers, Expr constant, String name,
				Collection<Attribute> attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.constant = constant;
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
			return "define " + constant + " as " + name;
		}
	}
	
	public static class TypeDecl extends SyntacticElement.Impl implements Decl {
		public final List<Modifier> modifiers;
		public final UnresolvedType type;	
		public final Expr constraint;
		public final String name;

		public TypeDecl(List<Modifier> modifiers, UnresolvedType type, String name, Expr constraint,
				Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.type = type;
			this.name = name;
			this.constraint = constraint;
		}

		public TypeDecl(List<Modifier> modifiers, UnresolvedType type, String name, Expr constraint,
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
		public final UnresolvedType receiverType;
		public final UnresolvedType returnType;
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
				UnresolvedType receiverType, UnresolvedType returnType,
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
				UnresolvedType receiverType, UnresolvedType returnType,
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
		public final UnresolvedType type;
		public final String name;

		public Parameter(UnresolvedType type, String name, Attribute... attributes) {
			super(attributes);
			this.type = type;
			this.name = name;
		}
		
		public String name() {
			return name;
		}
	}	
}
