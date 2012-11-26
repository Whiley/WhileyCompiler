package wyone.core;

import static wyone.util.SyntaxError.syntaxError;

import java.io.File;
import java.util.*;

import wyautl.core.Automata;
import wyautl.core.Automaton;
import wyone.util.Pair;
import static wyone.core.Types.*;

public class TypeExpansion {
	public void expand(SpecFile spec) {
		HashMap<String,Type.Term> terms = gatherTerms(spec);
		HashMap<String,Type> macros = gatherMacros(spec,terms);
		macros.putAll(terms);
		
		expandTypeDeclarations(spec,terms,macros);
		expandTypePatterns(spec,terms,macros);
	}
	
	protected void expandTypePatterns(SpecFile spec,
			HashMap<String, Type.Term> terms, HashMap<String, Type> macros) {
		for (SpecFile.Decl d : spec.declarations) {
			if (d instanceof SpecFile.IncludeDecl) {
				SpecFile.IncludeDecl id = (SpecFile.IncludeDecl) d;
				expandTypePatterns(id.file, terms, macros);
			} else if (d instanceof SpecFile.RewriteDecl) {
				SpecFile.RewriteDecl td = (SpecFile.RewriteDecl) d;
				td.pattern = expandAsPattern(td.pattern, spec, terms, macros);
			}
		}
	}
	
	protected void expandTypeDeclarations(SpecFile spec,
			HashMap<String, Type.Term> terms, HashMap<String, Type> macros) {
		for (SpecFile.Decl d : spec.declarations) {
			if (d instanceof SpecFile.IncludeDecl) {
				SpecFile.IncludeDecl id = (SpecFile.IncludeDecl) d;
				expandTypeDeclarations(id.file, terms, macros);
			} else if (d instanceof SpecFile.TermDecl) {
				SpecFile.TermDecl td = (SpecFile.TermDecl) d;
				td.type = (Type.Term) expandAsTerm(td.type.name(), spec, terms, macros);
			}
		}
	}
	
	protected HashMap<String,Type.Term> gatherTerms(SpecFile spec) {
		HashMap<String,Type.Term> map = new HashMap();
		gatherTerms(spec,map);
		return map;
	}
	
	protected void gatherTerms(SpecFile spec, HashMap<String, Type.Term> terms) {
		for (SpecFile.Decl d : spec.declarations) {
			if (d instanceof SpecFile.IncludeDecl) {
				SpecFile.IncludeDecl id = (SpecFile.IncludeDecl) d;
				gatherTerms(id.file, terms);
			} else if (d instanceof SpecFile.TermDecl) {
				SpecFile.TermDecl td = (SpecFile.TermDecl) d;
				String name = td.type.name();
				if (terms.get(name) != null) {
					syntaxError("type " + name + " is already defined",
							spec.file, td);
				}
				terms.put(name, td.type);
			}
		}
	}
	
	protected HashMap<String,Type> gatherMacros(SpecFile spec, HashMap<String, Type.Term> terms) {
		HashSet<String> openClasses = new HashSet<String>();
		HashMap<String,Type> macros = new HashMap<String,Type>();
		gatherMacros(spec,openClasses,macros,terms);
		return macros;
	}
	
	protected void gatherMacros(SpecFile spec, HashSet<String> openClasses,
			HashMap<String, Type> macros, HashMap<String, Type.Term> terms) {			
		// First, we have to inline all the type declarations.
		for (SpecFile.Decl d : spec.declarations) {
			if (d instanceof SpecFile.IncludeDecl) {
				SpecFile.IncludeDecl id = (SpecFile.IncludeDecl) d;
				gatherMacros(id.file, openClasses, macros, terms);				
			} else if (d instanceof SpecFile.TypeDecl) {
				SpecFile.TypeDecl cd = (SpecFile.TypeDecl) d;
				Type type = macros.get(cd.name);

				if (type != null && !openClasses.contains(cd.name)) {
					syntaxError("type " + cd.name + " is not open", spec.file,
							cd);
				} else if (type != null && !cd.isOpen) {
					syntaxError("type " + cd.name
							+ " cannot be closed (i.e. it's already open)",
							spec.file, cd);
				} else if (terms.containsKey(cd.name)) {
					syntaxError(cd.name + " is defined as a term", spec.file,
							cd);
				}

				if (type == null) {
					type = cd.type;
				} else {
					type = Type.T_OR(type,cd.type);
				}

				macros.put(cd.name, type);

				if (cd.isOpen) {
					openClasses.add(cd.name);
				}
			}
		}
	}
	
	protected Pattern expandAsPattern(Pattern pattern, SpecFile spec,
			HashMap<String, Type.Term> terms, HashMap<String, Type> macros) {

		if (pattern instanceof Pattern.Leaf) {
			return expandAsPattern((Pattern.Leaf) pattern, spec, terms, macros);
		} else if (pattern instanceof Pattern.Collection) {
			return expandAsPattern((Pattern.Collection) pattern, spec, terms,
					macros);
		} else {
			return expandAsPattern((Pattern.Term) pattern, spec, terms, macros);
		}
	}
	
	protected Pattern.Leaf expandAsPattern(Pattern.Leaf pattern, SpecFile spec,
			HashMap<String, Type.Term> terms, HashMap<String, Type> macros) {
		pattern.type = expandAsType(pattern.type, macros);		
		return pattern;
	}
	
	protected Pattern.Collection expandAsPattern(Pattern.Collection pattern,
			SpecFile spec, HashMap<String, Type.Term> terms,
			HashMap<String, Type> macros) {
		Pair<Pattern, String>[] pattern_elements = pattern.elements;
		for (int i = 0; i != pattern_elements.length; ++i) {
			Pair<Pattern, String> p = pattern_elements[i];
			Pattern pat = expandAsPattern(p.first(), spec, terms, macros);
			pattern_elements[i] = new Pair<Pattern, String>(pat, p.second());
		}
		return pattern;
	}
	
	protected Pattern.Term expandAsPattern(Pattern.Term pattern, SpecFile spec,
			HashMap<String, Type.Term> terms, HashMap<String, Type> macros) {
		if(!terms.containsKey(pattern.name)) {
			syntaxError(pattern.name + " is not defined as a term", spec.file,
					pattern);
		}		
		pattern.data = expandAsPattern(pattern.data,spec,terms,macros);		
		return pattern;
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
	 * @return
	 */
	protected Type.Term expandAsTerm(String name, SpecFile spec,
			HashMap<String, Type.Term> terms, HashMap<String, Type> macros) {
		Type.Term type = terms.get(name);
		type = (Type.Term) expandAsType(type,macros);
		
		terms.put(name, type);

		return type;
	}

	protected Type expandAsType(Type type, HashMap<String, Type> macros) {
		Automaton automaton = type.automaton();
		HashMap<String, Integer> roots = new HashMap<String, Integer>();
		HashMap<Integer,Integer> visited = new HashMap<Integer,Integer>();
		
		ArrayList<Automaton.State> states = new ArrayList<Automaton.State>();
		for(int i=0;i!=automaton.nStates();++i) {
			states.add(automaton.get(i).clone());
		}
		int root = expand(automaton.getRoot(0), states, visited,
				roots, macros);
		automaton = new Automaton(states.toArray(new Automaton.State[states.size()]));
		automaton.setRoot(0, root);
		return Type.construct(automaton);
	}
	
	/**
	 * <p>
	 * Traverse the automaton representing a type in the source code, and expand
	 * any macros encountered. Care must be take to protect against infinite
	 * recursion in the case of a macro that contains itself (i.e. a recursive
	 * type).
	 * </p>
	 * 
	 * <p>
	 * Unfortunately, this function is significantly complicated by the fact
	 * that the types of the rewrite language are encoded into the language of
	 * the automaton. It gets complicated because the types being encoded are
	 * talking about the constructs in which they are encoded.
	 * </p>
	 * 
	 * @param node
	 *            --- Current node being visited.
	 * @param automaton
	 *            --- Automaton being traversed.
	 * @param visited
	 *            --- Flags used to identify nodes already visited and, hence,
	 *            to protect against infinite recursion.
	 * @param roots
	 *            --- A mapping from previously expanded macro names to the
	 *            locatin where they were expanded. This is used to ensure that
	 *            when a macro is expanded for the second time, we actually just
	 *            reference the existing expansion point. Whilst this acts as a
	 *            useful space optimisation, it's actually critical to protect
	 *            against infinite recursion for macros which contain
	 *            themselves.
	 * @param macros
	 *            --- A mapping from macro names to the types representing their
	 *            expansions.
	 * @return
	 */
	protected int expand(int node, ArrayList<Automaton.State> states,
			HashMap<Integer, Integer> visited, HashMap<String, Integer> roots,
			HashMap<String, Type> macros) {
		if(node < 0) {
			return node;
		} else if(visited.containsKey(node)) {
			return visited.get(node);
		} else {
			// we haven't visited this node before, so visit it!			
			visited.put(node, node);
			
			Automaton.State state = states.get(node);
			
			if (state instanceof Automaton.Constant) {
				// Constant states 
			} else if (state instanceof Automaton.Collection) {
				Automaton.Collection ac = (Automaton.Collection) state;
				int[] nelements = new int[ac.size()];
				for (int i = 0; i != nelements.length; ++i) {
					nelements[i] = expand(ac.get(i), states, visited, roots,
							macros);
				}
				if (state instanceof Automaton.Set) {
					state = new Automaton.Set(nelements);
				} else if (state instanceof Automaton.Bag) {
					state = new Automaton.Bag(nelements);
				} else {
					state = new Automaton.List(nelements);
				}
				states.set(node, state);
			} else {
				Automaton.Term t = (Automaton.Term) state;
				int ncontents = t.contents;
				if (t.kind == K_Term) {
					// potentially hard if this is a macro.
					Automaton.List l = (Automaton.List) states.get(t.contents);
					Automaton.Strung s = (Automaton.Strung) states.get(l
							.get(0));
					String name = s.value;
					int contents = l.size() > 1 ? l.get(1) : Automaton.K_VOID;
					
					Type macro = macros.get(name);

					if(contents != Automaton.K_VOID && !(macro instanceof Type.Term)) {
						// Currently, you cannot attempt to specialise a macro
						// by supplying an operand. In the future, it would be
						// nice to support this.
						throw new RuntimeException("Cannot provide an operand to a macro");
					} else if(contents != Automaton.K_VOID) {
						// Term with non-void argument so expand as per normal.
						// We start from ncontents, not contents, since we want
						// to preserve the existing structure.
						ncontents = expand(ncontents, states, visited, roots,
								macros);
					} else if(roots.containsKey(name)) {
						// In this case, we have a previously expanded macro.
						// Therefore, we just return the location where it was
						// previously expanded. Obvserve that this is more than
						// just a space-saving optimisation: it's critical to
						// prevent infinite loops in the case of recursive types.
						return roots.get(name);
					} else if (macro instanceof Type.Term) {
						Type.Term mt = (Type.Term) macro; 

						if(mt.element() == null) {
							// in this case, we have an atom (i.e. a term which does
							// not have an argument). Thus, we should not need to
							// expand to a nominal type as this is unnecessary (and,
							// in fact, will break the assumption that terms always
							// produce terms).
						} else {
							// In this, we have a term which is specified to
							// have an argument, but for which no argument is
							// given. In which case, we simply expand the term
							// to include its default argument type.
							Automaton macro_automaton = macro.automaton();
							int root = Automata.extract(macro_automaton, macro_automaton.getRoot(0), states);							
							// We store the location of the expanded macro into the
							// roots cache so that it can be reused if/when we
							// encounter the same macro again.
							roots.put(name, root);	
							visited.put(node, root);
							return expand(root, states, visited, roots, macros);
						}
					} else if (macro != null && !(macro instanceof Type.Term)) {
						// In this case, we have identified a nominal type which
						// should be inlined into this automaton and then
						// recursively expanded as necessary.
						Automaton macro_automaton = macro.automaton();
						int element =  Automata.extract(macro_automaton, macro_automaton.getRoot(0), states);
						int base = states.size();
						states.add(new Automaton.Strung(name));
						states.add(new Automaton.List(base, element));
						states.add(new Automaton.Term(K_Nominal,
								base+1));
						// We store the location of the expanded macro into the
						// roots cache so that it can be reused if/when we
						// encounter the same macro again.
						roots.put(name, base+2);
						visited.put(node, base+2);
						return expand(base+2, states, visited, roots, macros);
					} else {
						// This is not a macro, and should match a term which
						// does not accept operands. Therefore, we don't need to
						// do anything.
					}
				} else if (ncontents != Automaton.K_VOID) {
					// easy case
					ncontents = expand(ncontents, states, visited, roots,
							macros);
				} 

				states.set(node, new Automaton.Term(t.kind, ncontents));
			}
		}
		return node;
	}		
}
