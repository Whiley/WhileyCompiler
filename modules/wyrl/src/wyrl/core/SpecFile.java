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

package wyrl.core;

import java.io.File;
import java.util.*;

import wyrl.core.Pattern.Term;
import wyrl.util.*;

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

	public static class TypeDecl extends SyntacticElement.Impl implements Decl {
		public final String name;
		public final Type type;
		public final boolean isOpen;

		public TypeDecl(String n, Type type, boolean isOpen, Attribute... attributes) {
			super(attributes);
			this.name = n;
			this.type = type;
			this.isOpen = isOpen;
		}
	}

	public static abstract class RewriteDecl extends SyntacticElement.Impl implements
			Decl {
		public Pattern.Term pattern;
		public final ArrayList<RuleDecl> rules;
		public final String name;
		public final int rank;

		public RewriteDecl(Pattern.Term pattern, Collection<RuleDecl> rules,
				String name, int rank, Attribute... attributes) {
			super(attributes);
			this.pattern = pattern;
			this.rules = new ArrayList<RuleDecl>(rules);
			this.name = name;
			this.rank = rank;
		}
	}

	public static class ReduceDecl extends RewriteDecl {
		public ReduceDecl(Pattern.Term pattern, Collection<RuleDecl> rules,
				String name, int rank, Attribute... attributes) {
			super(pattern,rules,name,rank,attributes);
		}
	}

	public static class InferDecl extends RewriteDecl {
		public InferDecl(Pattern.Term pattern, Collection<RuleDecl> rules,
				String name, int rank, Attribute... attributes) {
			super(pattern,rules,name,rank, attributes);
		}
	}

	public static class RuleDecl extends SyntacticElement.Impl implements SyntacticElement {
		public final ArrayList<Pair<String,Expr>> lets;
		public Expr result;
		public Expr condition;

		public RuleDecl(Collection<Pair<String, Expr>> lets, Expr result,
				Expr condition, Attribute... attributes) {
			super(attributes);
			this.lets = new ArrayList<Pair<String,Expr>>(lets);
			this.result = result;
			this.condition = condition;
		}
	}

	public static class FunctionDecl extends SyntacticElement.Impl implements Decl {
		public final String name;
		public final Type from;
		public final Type to;

		public FunctionDecl(String name, Type from, Type to, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.from = from;
			this.to = to;
		}
	}
}
