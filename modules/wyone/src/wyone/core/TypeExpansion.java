package wyone.core;

import static wyone.util.SyntaxError.syntaxError;

import java.io.File;
import java.util.*;

public class TypeExpansion {
	public void expand(SpecFile spec) {
		HashMap<String,Type> types = buildTypeMap(spec);
		HashSet<String> expanded = new HashSet<String>();
		expandTypeDeclarations(spec,types, expanded);
	}
	
	protected void expandTypeDeclarations(SpecFile spec, HashMap<String, Type> types,
			HashSet<String> expanded) {
		for (SpecFile.Decl d : spec.declarations) {
			if (d instanceof SpecFile.IncludeDecl) {
				SpecFile.IncludeDecl id = (SpecFile.IncludeDecl) d;
				gatherTerms(id.file, types);
			} else if (d instanceof SpecFile.TermDecl) {
				SpecFile.TermDecl td = (SpecFile.TermDecl) d;
				td.type = (Type.Term) expand(td.type.name(), spec, types, expanded);
			}
		}
	}
	
	protected HashMap<String, Type> buildTypeMap(SpecFile spec) {
		HashSet<String> openClasses = new HashSet<String>();
		HashMap<String, Type> types = new HashMap();
		gatherTypeClasses(spec, openClasses, types);	
		gatherTerms(spec,types);
		return types;
	}
	
	protected void gatherTerms(SpecFile spec, HashMap<String, Type> types) {
		for (SpecFile.Decl d : spec.declarations) {
			if (d instanceof SpecFile.IncludeDecl) {
				SpecFile.IncludeDecl id = (SpecFile.IncludeDecl) d;
				gatherTerms(id.file, types);
			} else if (d instanceof SpecFile.TermDecl) {
				SpecFile.TermDecl td = (SpecFile.TermDecl) d;
				String name = td.type.name();
				if (types.get(name) != null) {
					syntaxError("type " + name + " is already defined",
							spec.file, td);
				}
				types.put(name, td.type);
			}
		}
	}
	
	protected void gatherTypeClasses(SpecFile spec, HashSet<String> openClasses, HashMap<String,Type> types) {			
		// First, we have to inline all the type declarations.
		for (SpecFile.Decl d : spec.declarations) {
			if (d instanceof SpecFile.IncludeDecl) {
				SpecFile.IncludeDecl id = (SpecFile.IncludeDecl) d;
				gatherTypeClasses(id.file, openClasses, types);				
			} else if (d instanceof SpecFile.ClassDecl) {
				SpecFile.ClassDecl cd = (SpecFile.ClassDecl) d;
				Type type = types.get(cd.name);

				if (type != null && !openClasses.contains(cd.name)) {
					syntaxError("type " + cd.name + " is not open", spec.file,
							cd);
				} else if (type != null && !cd.isOpen) {
					syntaxError("type " + cd.name
							+ " cannot be closed (i.e. it's already open)",
							spec.file, cd);
				}

				if (type == null) {
					type = cd.type;
				} else {
					type = Type.T_OR(type,cd.type);
				}

				types.put(cd.name, type);

				if (cd.isOpen) {
					openClasses.add(cd.name);
				}
			}
		}
	}
	
	/**
	 * Fully expand the type associated with a given name. The name must be a
	 * key into the <code>types</code> map. In the case that the name is already
	 * in the <code>expanded</code> set, then type it currently maps to is
	 * returned. Otherwise, the type is traversed and all subcomponents
	 * expanded.
	 * 
	 * @param name
	 *            --- name of type to expand.
	 * @param spec
	 *            --- spec file containing type.
	 * @param types
	 *            --- types map to be updated with expanded type.
	 * @param expanded
	 *            --- set of previously expanded types.
	 * @return
	 */
	protected Type expand(String name, SpecFile spec,
			HashMap<String, Type> types, HashSet<String> expanded) {
		Type type = types.get(name);

		if (expanded.contains(name)) {
			return type;
		} else {
			type = expand(type, spec, types, expanded);
		}

		types.put(name, type);
		expanded.add(name);

		return type;
	}

	protected Type expand(Type type, SpecFile spec, HashMap<String,Type> types, HashSet<String> expanded) {
		
		// TODO: this could be made more efficient by walking the automaton
		// directly.
		// FIXME: this doesn't handle cyclic structures properly.
		
		if(type instanceof Type.Atom) {
			// do nothing
			return type;
		} else if(type instanceof Type.Unary) {
			Type.Unary tu = (Type.Unary) type; 
			Type element = expand(tu.element(),spec,types,expanded);
			
			if(type instanceof Type.Meta) {
				return Type.T_META(element);
			} else if(type instanceof Type.Ref) {
				return Type.T_REF(element);
			} else {
				return Type.T_NOT(element);
			}
		} else if(type instanceof Type.Nary) {
			Type.Nary tu = (Type.Nary) type;			
			Type[] elements = tu.elements();
			for(int i=0;i!=elements.length;++i) {
				elements[i] = expand(elements[i],spec,types,expanded);
			}
			if(type instanceof Type.And) {
				return Type.T_AND(elements);
			} else if(type instanceof Type.Or) {
				return Type.T_OR(elements);
			} else {
				return Type.T_FUN(elements[0],elements[1]);
			}
		} else if(type instanceof Type.Compound) {
			Type.Compound tu = (Type.Compound) type;
			boolean unbounded = tu.unbounded();
			Type[] elements = tu.elements();
			for(int i=0;i!=elements.length;++i) {
				elements[i] = expand(elements[i],spec,types,expanded);
			}
			if(type instanceof Type.Set) {
				return Type.T_SET(unbounded,elements);
			} else if(type instanceof Type.Bag) {
				return Type.T_BAG(unbounded,elements);
			} else {
				return Type.T_LIST(unbounded,elements);
			}
		} else {
			Type.Term tu = (Type.Term) type;
			String name = tu.name();
			if(types.get(name) instanceof Type.Term) {
				Type.Ref element = tu.element();
				if(element != null) {
					// FIXME: is result guaranteed to be Type.Ref?
					element = (Type.Ref) expand(element,spec,types,expanded);
				}
				return Type.T_TERM(name, element);
			} else {
				return expand(tu.name(),spec,types,expanded);
			}
		}		
	}	
}
