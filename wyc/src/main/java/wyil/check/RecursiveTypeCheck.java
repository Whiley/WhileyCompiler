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

import static wyil.lang.WyilFile.*;

import java.util.*;

import jbfs.core.Build;
import wycc.util.AbstractCompilationUnit.Tuple;
import wyil.lang.Compiler;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl.Link;
import wyil.util.AbstractVisitor;

/**
 * Determine the <i>positional variance</i> of template parameters in all
 * declared types. For example:
 *
 * <pre>
 * type Box<T> is { T value }
 * </pre>
 *
 * In this case, template variable <code>T</code> is considered to be
 * <i>covariant</i>, meaning that <code>Box&lt;int&gt;</code> is a subtype of
 * <code>Box&lt;int|bool&gt;</code>. In contrast, consider:
 *
 * <pre>
 * type Box<T> is { &T value }
 * </pre>
 *
 * In this case, template variable <code>T</code> is considered to be
 * <i>invariant</i>, meaning that <code>Box&lt;int&gt;</code> is not a subtype
 * of <code>Box&lt;int|bool&gt;</code> and neither is
 * <code>Box&lt;int|bool&gt;</code> is not a subtype of
 * <code>Box&lt;intint&gt;</code>, etc. Finally, consider:
 *
 * <pre>
 * type pred_t<T> is function(T)->(bool)
 * </pre>
 *
 * In this case, template variable <code>T</code> is considered to be
 * <i>contravariant</i>, meaning that <code>pred_t&lt;int|bool&gt;</code> is a
 * subtype of <code>pred_t&lt;int&gt;</code>.
 *
 * @author David J. Pearce
 *
 */
public class RecursiveTypeCheck extends AbstractVisitor implements Compiler.Check {
	private boolean status = true;

	public RecursiveTypeCheck(Build.Meter meter) {
		super(meter.fork(RecursiveTypeCheck.class.getSimpleName()));
	}

	@Override
	public boolean check(WyilFile wf) {
		visitModule(wf);
		meter.done();
		return status;
	}

	@Override
	public void visitExternalUnit(Decl.Unit unit) {
		// NOTE: we override this to prevent unnecessarily traversing units
	}

	@Override
	public void visitDeclaration(Decl decl) {
		if (decl instanceof Decl.Type) {
			Decl.Type t = (Decl.Type) decl;
			check(t, new HashSet<>());
		}
	}

	private void check(Decl.Type decl, Set<QualifiedName> cache) {
		Tuple<Template.Variable> template = decl.getTemplate();
		cache.add(decl.getQualifiedName());
		//
		for (int i = 0; i != template.size(); ++i) {
			Template.Variable ith = template.get(i);
			if (ith.getVariance() == Template.Variance.UNKNOWN) {
				// Determine variance for ith parameter position
				Template.Variance ith_variance = infer(ith.getName(), decl.getVariableDeclaration().getType(), cache);
				// Done
				ith.setVariance(ith_variance);
			}
		}
		// Done
		cache.remove(decl.getQualifiedName());
	}

	protected Template.Variance infer(Identifier variable, Type type, Set<QualifiedName> cache) {
		meter.step("infer");
		switch (type.getOpcode()) {
		case TYPE_any:
		case TYPE_bool:
		case TYPE_byte:
		case TYPE_int:
		case TYPE_null:
		case TYPE_void:
			return Template.Variance.UNKNOWN;
		case TYPE_universal: {
			Type.Universal t = (Type.Universal) type;
			if (t.getOperand().equals(variable)) {
				return Template.Variance.COVARIANT;
			} else {
				return Template.Variance.UNKNOWN;
			}
		}
		case TYPE_array: {
			Type.Array t = (Type.Array) type;
			return infer(variable, t.getElement(), cache);
		}
		case TYPE_reference: {
			Type.Reference t = (Type.Reference) type;
			Template.Variance v = infer(variable, t.getElement(), cache);
			if (v != Template.Variance.UNKNOWN) {
				return Template.Variance.INVARIANT;
			} else {
				return v;
			}
		}
		case TYPE_function:
		case TYPE_method:
		case TYPE_property: {
			Type.Callable t = (Type.Callable) type;
			Template.Variance p = infer(variable, t.getParameter(), cache);
			Template.Variance r = infer(variable, t.getReturn(), cache);
			// NOTE: must invert p here
			return join(invert(p), r);
		}
		case TYPE_nominal: {
			Type.Nominal t = (Type.Nominal) type;
			Link<Decl.Type> link = t.getLink();
			if (!link.isResolved()) {
				// This nominal was unable to be resolved during NameResolution and, hence, this
				// indicates an error up stream.
				return Template.Variance.UNKNOWN;
			} else if (cache.contains(link.getTarget().getQualifiedName())) {
				// Indicates a recursive type encountered
				return Template.Variance.COVARIANT;
			} else {
				// Infer variance of nominal
				Decl.Type target = link.getTarget();
				check(target, cache);
				Tuple<Template.Variable> template = target.getTemplate();
				Tuple<Type> parameters = t.getParameters();
				Template.Variance variance = Template.Variance.UNKNOWN;
				for (int i = 0; i != Math.min(template.size(), parameters.size()); ++i) {
					Template.Variable ith = template.get(i);
					Type ith_param = parameters.get(i);
					Template.Variance v = infer(variable, ith_param, cache);
					variance = join(variance, apply(v, ith));
				}
				return variance;
			}
		}
		case TYPE_tuple: {
			Type.Tuple t = (Type.Tuple) type;
			return infer(variable, t.getAll(), cache);
		}
		case TYPE_union: {
			Type.Union t = (Type.Union) type;
			return infer(variable, t.getAll(), cache);
		}
		case TYPE_record: {
			Type.Record t = (Type.Record) type;
			return infer(variable, t.getFields(), cache);
		}
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	private Template.Variance infer(Identifier variable, Type[] types, Set<QualifiedName> cache) {
		Template.Variance r = Template.Variance.UNKNOWN;
		for (int i = 0; i != types.length; ++i) {
			r = join(r, infer(variable, types[i], cache));
		}
		return r;
	}

	private Template.Variance infer(Identifier variable, Tuple<Type.Field> fields, Set<QualifiedName> cache) {
		Template.Variance r = Template.Variance.UNKNOWN;
		for (int i = 0; i != fields.size(); ++i) {
			r = join(r, infer(variable, fields.get(i).getType(), cache));
		}
		return r;
	}

	private static Template.Variance apply(Template.Variance v, Template.Variable t) {
		if (v == Template.Variance.UNKNOWN) {
			return v;
		} else {
			switch (t.getVariance()) {
			case INVARIANT:
				return Template.Variance.INVARIANT;
			case COVARIANT:
				return v;
			case CONTRAVARIANT:
				return invert(v);
			default:
				return Template.Variance.UNKNOWN;
			}
		}
	}

	private static Template.Variance join(Template.Variance lhs, Template.Variance rhs) {
		if (lhs == Template.Variance.UNKNOWN) {
			return rhs;
		} else if (rhs == Template.Variance.UNKNOWN) {
			return lhs;
		} else if (lhs == rhs) {
			return lhs;
		} else {
			return Template.Variance.INVARIANT;
		}
	}

	private static Template.Variance invert(Template.Variance v) {
		switch (v) {
		case COVARIANT:
			return Template.Variance.CONTRAVARIANT;
		case CONTRAVARIANT:
			return Template.Variance.COVARIANT;
		default:
			return v;
		}
	}
}
