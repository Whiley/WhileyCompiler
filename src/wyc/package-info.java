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
