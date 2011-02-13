package wyone.core;

import java.util.*;
import wyil.util.*;
import wyil.lang.Attribute;

public class SpecFile {
	public final String filename;
	public final ArrayList<Decl> declarations;
	
	public SpecFile(String filename, Collection<Decl> declarations) {
		this.filename = filename;
		this.declarations = new ArrayList<Decl>(declarations);
	}
	
	public interface Decl extends SyntacticElement {}
	
	public static class TermDecl extends SyntacticElement.Impl implements Decl {
		public final String name;
		
		public TermDecl(String n, Attribute... attributes) {
			super(attributes);
			this.name = n;
		}		
	}
	
	public static class ClassDecl extends SyntacticElement.Impl implements Decl {
		public final String name;
		public final List<String> children;
		
		public ClassDecl(String n, Collection<String> children, Attribute... attributes) {
			super(attributes);
			this.name = n;
			this.children = new ArrayList<String>(children);
		}				
	}
	
	public static class RewriteDecl extends SyntacticElement.Impl implements Decl {
		public final String name;
		public final ArrayList<Pair<TypeDecl,String>> types;
		
		public RewriteDecl(String n, Collection<Pair<TypeDecl,String>> types, 
				Collection<RuleDecl> rules,
				Attribute... attributes) {
			super(attributes);
			this.name = n;
			this.types = new ArrayList(types);			
		}		
	}		
	
	public static class RuleDecl extends SyntacticElement.Impl implements SyntacticElement {
		public final Expr result;
		public final Expr condition;
		
		public RuleDecl(Expr result, Expr condition, Attribute... attributes) {
			super(attributes);
			this.result = result;
			this.condition = condition;
		}	
	}

	public static class TypeDecl extends SyntacticElement.Impl implements SyntacticElement {
		public final Type type;
		
		public TypeDecl(Type type, Attribute... attributes) {
			super(attributes);
			this.type = type;
		}		
	}
}
