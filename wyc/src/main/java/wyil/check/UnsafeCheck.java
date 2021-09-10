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

import wycc.lang.Build;
import wycc.lang.SyntacticItem;
import wyil.lang.Compiler;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.util.AbstractConsumer;
import wyil.util.AbstractVisitor;
import wyc.util.ErrorMessages;
import static wyil.lang.WyilFile.*;

import java.util.HashSet;
public class UnsafeCheck extends AbstractVisitor implements Compiler.Check {
	private boolean status = true;

	public UnsafeCheck(Build.Meter meter) {
		super(meter.fork(UnsafeCheck.class.getSimpleName()));
	}

	@Override
	public boolean check(WyilFile file) {
		visitModule(file);
		meter.done();
		return status;
	}

	@Override
	public void visitInvoke(Expr.Invoke expr) {
		// Check whether invoking an impure method in a pure context
		Decl.Link<Decl.Callable> name = expr.getLink();
		Decl.Named<?> enclosing = expr.getAncestor(Decl.Named.class);
		//
		if(isUnsafe(name.getTarget()) && !isUnsafe(enclosing)) {
			syntaxError(expr, UNSAFECALL_NOT_PERMITTED);
		}
		super.visitInvoke(expr);
	}

	@Override
	public void visitType(Type type) {
		// NOTE: don't traverse types as this is unnecessary. Even in a pure context,
		// seemingly impure types (e.g. references and methods) can still be used
		// safely.
	}

	public boolean isUnsafe(Decl.Named<?> named) {
		return named.getModifiers().match(Modifier.Unsafe.class) != null;
	}

	private void syntaxError(SyntacticItem e, int code, SyntacticItem... context) {
		status = false;
		ErrorMessages.syntaxError(e, code, context);
	}
}
