// This file is part of the Wyjvm bytecode manipulation library.
//
// Wyjvm is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyjvm is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyjvm. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjvm.lang;

import java.util.*;

/**
 * A modifier represents a flag (e.g. public/final/static) which can be used in
 * a variety of places, including on classes, methods and variable definitions.
 * 
 * @author djp
 * 
 */
public interface Modifier {

	// Constants are provided to same memory in the common case.
	public static final Modifier ACC_PUBLIC = new Public();
	public static final Modifier ACC_PRIVATE = new Private();
	public static final Modifier ACC_PROTECTED = new Protected();
	public static final Modifier ACC_ABSTRACT = new Abstract();
	public static final Modifier ACC_NATIVE = new Native();
	public static final Modifier ACC_SYNCHRONIZED = new Synchronized();
	public static final Modifier ACC_SUPER = new Super();
	public static final Modifier ACC_INTERFACE = new Interface();
	public static final Modifier ACC_SYNTHETIC = new Synthetic();
	public static final Modifier ACC_ANNOTATION = new AnnotationT();
	public static final Modifier ACC_ENUM = new Enum();
	public static final Modifier ACC_TRANSIENT = new Transient();
	public static final Modifier ACC_STATIC = new Static();
	public static final Modifier ACC_VARARGS = new VarArgs();	
	public static final Modifier ACC_VOLATILE = new Volatile();
	public static final Modifier ACC_STRICT = new StrictFP();
	public static final Modifier ACC_FINAL = new Final();
	public static final Modifier ACC_BRIDGE = new Bridge();

	public final static class Public implements Modifier {}
	public final static class Protected implements Modifier {}
	public final static class Private implements Modifier {}
	public final static class StrictFP implements Modifier {}
	public final static class Static implements Modifier {}
	public final static class Abstract implements Modifier {}
	public final static class Final implements Modifier {}
	public final static class Native implements Modifier {}
	public final static class Synchronized implements Modifier {}
	public final static class Transient implements Modifier {}
	public final static class Volatile implements Modifier {}
	public final static class Interface implements Modifier {}
	public final static class Synthetic implements Modifier {}	
	public final static class AnnotationT implements Modifier {}	
	public final static class Enum implements Modifier {}	
	public final static class Super implements Modifier {}	
	public final static class Bridge implements Modifier {}	
	
	/**
	 * A varargs modifier is used to indicate that a method has variable-length
	 * arity. In the Java ClassFile format, this is written as ACC_TRANSIENT,
	 * although it's simpler for us to distinguish these things properly.
	 */
	public final static class VarArgs implements Modifier {}	
}