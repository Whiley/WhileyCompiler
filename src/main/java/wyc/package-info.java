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
/**
 * <b>The Whiley Compiler Front End</b>.  This provides the front-end of any Whiley Compiler,
 * and is responsible for tasks such as lexing, parsing, type checking and WYIL generation.
 * The compiler front-end also includes a notion of <i>pipeline</i> for linking different
 * stages of the compiler together.  A standard interface for manipulating the pipeline
 * and registering new pipeline stages is given.
 *
 * @author David J. Pearce
 */
package wyc;
