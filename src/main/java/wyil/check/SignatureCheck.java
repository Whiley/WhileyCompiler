// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyil.check;

import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyil.lang.Compiler;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.*;
import wyil.util.AbstractConsumer;
import wyil.util.TypeMangler;
import wyc.util.ErrorMessages;

import java.util.HashSet;

/**
 * <p>
 * Responsible for checking no duplicate declarations exist. That is,
 * declarations with the same signature. For example, the following is
 * prohibited:
 *
 * <pre>
 * function f(int x) -> (int r):
 *    return x+1
 *
 * function f(int x) -> (int r):
 *    return x-1
 * </pre>
 *
 * There are a few special cases. For example, `export` declarations have no
 * mangle information associated with them at all.
 * </p>
 *
 *
 * @author David J. Pearce
 *
 */
public class SignatureCheck extends AbstractConsumer<SignatureCheck.Context> implements Compiler.Check {
	private static final TypeMangler mangler = new TypeMangler.Default();
	private boolean status = true;

	@Override
	public boolean check(WyilFile file) {
		visitModule(file, new Context());
		return status;
	}

	@Override
	public void visitType(Decl.Type decl, Context data) {
		if(!data.register(decl)) {
			syntaxError(decl.getName(),WyilFile.DUPLICATE_DECLARATION);
		}
	}

	@Override
	public void visitProperty(Decl.Property decl, Context data) {
		if(!data.register(decl)) {
			syntaxError(decl.getName(),WyilFile.DUPLICATE_DECLARATION);
		}
	}

	@Override
	public void visitFunction(Decl.Function decl, Context data) {
		if(!data.register(decl)) {
			syntaxError(decl.getName(),WyilFile.DUPLICATE_DECLARATION);
		}
	}

	@Override
	public void visitMethod(Decl.Method decl, Context data) {
		if(!data.register(decl)) {
			syntaxError(decl.getName(),WyilFile.DUPLICATE_DECLARATION);
		}
	}

	@Override
	public void visitStaticVariable(Decl.StaticVariable decl, Context data) {
		if(!data.register(decl)) {
			syntaxError(decl.getName(),WyilFile.DUPLICATE_DECLARATION);
		}
	}

	/**
	 * A simple store of name mangles used to identify any clashes.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Context {
		private HashSet<String> mangles = new HashSet<>();

		/**
		 * Register a new named declaration, and check whether or not it clashes with a
		 * previously registered declaration.
		 *
		 * @param d
		 * @return true if this name was successfully registered without a clash.
		 */
		public boolean register(Decl.Named d) {
			String mangle = toMangledName(d);
			return mangles.add(mangle);
		}

		/**
		 * Determine the appropriate mangled string for a given named declaration. This
		 * is critical to ensuring that overloaded declarations do not clash.
		 *
		 * @param decl
		 * @return
		 */
		private String toMangledName(Decl.Named<?> decl) {
			// Determine whether this is an exported symbol or not
			boolean exported = decl.getModifiers().match(Modifier.Export.class) != null;
			// Construct base name
			String name = decl.getQualifiedName().toString().replace("::", "$");
			// Add type mangles for non-exported symbols
			if(!exported && decl instanceof Decl.Method) {
				Decl.Method method = (Decl.Method) decl;
				Tuple<Type> parameters = method.getType().getParameters();
				Tuple<Identifier> lifetimes = method.getType().getLifetimeParameters();
				name += getMangle(parameters, lifetimes);
			} else if(!exported && decl instanceof Decl.Callable) {
				Decl.Callable callable = (Decl.Callable) decl;
				Tuple<Type> parameters = callable.getType().getParameters();
				name += getMangle(parameters, new Tuple<>());
			} else if(decl instanceof Decl.Type) {
				name += "$type";
			} else if(decl instanceof Decl.StaticVariable) {
				name += "$static";
			}
			return name;
		}


		private String getMangle(Tuple<Type> types, Tuple<Identifier> lifetimes) {
			if (types.size() == 0) {
				return "";
			} else {
				return "$" + mangler.getMangle(types, lifetimes);
			}
		}
	}

	private void syntaxError(SyntacticItem e, int code, SyntacticItem... context) {
		status = false;
		ErrorMessages.syntaxError(e, code, context);
	}
}
