// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.lang;

import wyal.lang.WyalFile;

/**
 * <p>
 * Provides classes for representing types in Whiley's source language. These
 * are referred to as <i>unresolved types</i> as they include nominal types
 * whose full NameID remains unknown. Unresolved types are <i>resolved</i>
 * during the name resolution> stage of the compiler.
 * </p>
 *
 * <p>
 * Each class is an instance of <code>SyntacticElement</code> and, hence, can be
 * adorned with certain information (such as source location, etc).
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface SyntacticType extends WyalFile.Type {

}

