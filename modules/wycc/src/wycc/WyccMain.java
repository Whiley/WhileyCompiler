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

package wycc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import wyc.WycMain;
import wyc.util.OptArg;
import wycc.util.WyccBuildTask;

/**
 * The main class provides all of the necessary plumbing to process command-line
 * options, construct an appropriate pipeline and then instantiate the Whiley
 * Compiler to generate class files.
 * 
 * @author David J. Pearce
 * 
 */
public class WyccMain {
	
	public static final OptArg[] WYCC_OPTIONS = new OptArg[]{
		new OptArg("debug", "Include debug information in generated C files."),
		new OptArg("no_numbers", "Suppress Whiley source line numbers in generated C files."),
		new OptArg("floats", "Support Whiley rational numbers using C floating point."),
		new OptArg("no_floats", "Suppress all C floating point."),
		new OptArg("only_indirect_calls", "Replace Invoke with IndirectInvoke.")
		
	};
	
	public static void main(String[] _args) {	
		ArrayList<String> args = new ArrayList<String>(Arrays.asList(_args));
		Map<String, Object> values = OptArg.parseOptions(args, WYCC_OPTIONS);
		
		boolean debug = values.containsKey("debug");
		if(debug) {
			System.out.println("GOT DEBUG!");
		}
		
		// FIXME: modify default options to include cdir
		System.exit(new WycMain(new WyccBuildTask(values), WycMain.DEFAULT_OPTIONS)
				.run(args.toArray(new String[args.size()])));
	}
}
