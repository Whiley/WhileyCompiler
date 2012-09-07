package wyone.spec;

import java.util.*;

import wyone.core.Attribute;
import wyone.core.Type;
import wyone.spec.Pattern.Term;
import wyone.util.*;

public class SpecFile {
	public final String pkg;
	public final String name;
	public final String filename;
	public final ArrayList<Decl> declarations;
	
	public SpecFile(String pkg, String name, String filename,
			Collection<Decl> declarations) {
		this.pkg = pkg;
		this.name = name;
		this.filename = filename;
		this.declarations = new ArrayList<Decl>(declarations);
	}
	
	public interface Decl extends SyntacticElement {}
	
	public static class TermDecl extends SyntacticElement.Impl implements Decl {
		public final Type.Term type;
		
		public TermDecl(Type.Term data, Attribute... attributes) {
			super(attributes);
			this.type = data;
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
	
	public static class RewriteDecl extends SyntacticElement.Impl implements
			Decl {
		public final Pattern.Term pattern;
		public final ArrayList<RuleDecl> rules;

		public RewriteDecl(Pattern.Term pattern, Collection<RuleDecl> rules,
				Attribute... attributes) {
			super(attributes);
			this.pattern = pattern;
			this.rules = new ArrayList<RuleDecl>(rules);
		}
	}
	
	public static class RuleDecl extends SyntacticElement.Impl implements SyntacticElement {
		public final ArrayList<Pair<String,Expr>> lets;
		public final Expr result;
		public final Expr condition;
		
		public RuleDecl(Collection<Pair<String,Expr>> lets, Expr result, Expr condition, Attribute... attributes) {
			super(attributes);
			this.lets = new ArrayList<Pair<String,Expr>>(lets);
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
