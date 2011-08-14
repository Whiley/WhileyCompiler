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

package wyjc.testing.benchmarks;

import java.io.IOException;

import wyjc.testing.BenchHarness;

public class ConcurrentBench extends BenchHarness {

  public ConcurrentBench() {
  	// There's a lot of variance. 10 might not be enough to reduce it to an
  	// acceptable level.
    super("../wybench/concurrent/micro", 10);
  }

  public void runSum() throws InterruptedException, IOException {
    System.out.println(runBench("sum/Main", "small.in"));
//    System.out.println(runBench("sum/Main", "medium.in"));
//    System.out.println(runBench("sum/Main", "large.in"));
  }
  
  public void runMatrix() throws InterruptedException, IOException {
  	System.out.println(runBench("matrix/Main", "small.in"));
  	System.out.println(runBench("matrix/Main", "medium.in"));
//  	System.out.println(runBench("matrix/Main", "large.in"));
  }

  public static void main(String[] args) throws InterruptedException,
      IOException {
    ConcurrentBench bench = new ConcurrentBench();
    bench.runSum();
  }

}
