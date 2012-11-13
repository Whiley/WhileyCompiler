package wyone.core;

import java.io.File;
import java.util.*;

import wyone.core.Pattern.Term;
import wyone.util.*;

public class SpecFile {
	public final String pkg;
	public final String name;
	public final File file;
	public final ArrayList<Decl> declarations;
	
	public SpecFile(String pkg, String name, File file,
			Collection<Decl> declarations) {
		this.pkg = pkg;
		this.name = name;
		this.file = file;
		this.declarations = new ArrayList<Decl>(declarations);
	}
	
	public interface Decl extends SyntacticElement {}
	
	public static class IncludeDecl extends SyntacticElement.Impl implements Decl {
		public final SpecFile file;
		
		public IncludeDecl(SpecFile file, Attribute... attributes) {
			super(attributes);
			this.file = file;
		}		
	}
	
	public static class TermDecl extends SyntacticElement.Impl implements Decl {
		public Type.Term type;
		
		public TermDecl(Type.Term data, Attribute... attributes) {
			super(attributes);
			this.type = data;
		}		
	}
	
	public static class ClassDecl extends SyntacticElement.Impl implements Decl {
		public final String name;
		public final Type type;
		public final boolean isOpen;
		
		public ClassDecl(String n, Type type, boolean isOpen, Attribute... attributes) {
			super(attributes);
			this.name = n;
			this.type = type;
			this.isOpen = isOpen;
		}				
	}
	
	public static abstract class RewriteDecl extends SyntacticElement.Impl implements
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
	
	public static class ReduceDecl extends RewriteDecl {
		public ReduceDecl(Pattern.Term pattern, Collection<RuleDecl> rules,
				Attribute... attributes) {
			super(pattern,rules,attributes);
		}
	}
	
	public static class InferDecl extends RewriteDecl {
		public InferDecl(Pattern.Term pattern, Collection<RuleDecl> rules,
				Attribute... attributes) {
			super(pattern,rules,attributes);
		}
	}
	
	public static class RuleDecl extends SyntacticElement.Impl implements SyntacticElement {
		public final ArrayList<Pair<String,Expr>> lets;
		public Expr result;
		public Expr condition;
		
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
