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