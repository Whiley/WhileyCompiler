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
use std::marker::PhantomData;
use syntactic_heap::{Ref};
use crate::{Bottom,Top};


// ===================================================================
// Constraint Var
// ===================================================================

#[derive(Clone,Copy,Debug)]
pub struct Var(usize);

// ===================================================================
// Candidate Solution
// ===================================================================

/// Represents a range of candidate solution(s) for a given variable.
/// More specifically, any type between the _upperbound_ and the
/// _lowerbound_ is a valid solution for the given variable.  However,
/// if not such type exists, then the variable's type is said to be
/// _unsatisfiable_.
#[derive(Clone,Copy,Debug)]
struct Entry {
    upperbound: usize,
    lowerbound: usize
}

/**
 * Represents a solution to a set of subtyping constraints. Each type variable
 * has a given lower and upper bound. As the solution evolves, these bounds are
 * narrowed down. If we end up where the lower bound is not a subtype of the
 * upper bound for some variable, then the solution is invalid.
 *
 * @author David J. Pearce
 */
#[derive(Debug)]
pub struct Solution<T> {
    dummy: PhantomData<T>,
    /// Constant identifying top type
    top: usize,
    /// Constant identifying bottom type
    bottom: usize,
    /// Current variable typings (which are references into the
    /// syntactic heap).  Each entry consists of an upper bound, and
    /// a lower bound.
    typing: Vec<Entry>    
}

impl<T:Copy+Bottom+Top> Solution<T> {
    pub fn new(top: usize, bottom: usize) -> Self {
        Self {
            dummy: PhantomData,
            top,bottom,
            typing: Vec::new()
        }
    }

    /// Check whether this typing is currently _satisfiable_ or not.
    /// More specifically, that is whether or not every variable has
    /// a satisfiable type.
    pub fn ok(&self) -> bool {
        for i in 0 .. self.typing.len() {
            let ith = self.typing[i];
            if !self.is_subtype(ith.upperbound,ith.lowerbound) {
                return false;
            }
        }
        true
    }
    
    /// Allocate a fresh (and entirely unconstrained) variable.    
    pub fn fresh_var(&mut self) -> Var {
        // Determine index of new variable
        let index = self.typing.len();
        // Initialise new variable
        self.typing.push(Entry{upperbound: self.top, lowerbound: self.bottom});
        // Done
        Var(index)
    }
           
    pub fn constrain_above<'a>(&mut self, supvar: Var, bound: Ref<'a,T>) -> bool {
        // Get variable's candidate solutions
        let entry = self.typing[supvar.0];
        // Determine glb
        let glb = self.greatest_lower_bound(entry.lowerbound,bound.raw_index());
        //
        if glb != entry.lowerbound {
            // Changed!
            self.typing[supvar.0].lowerbound = glb;
        }
        // Check whether still satisfiable
        self.is_subtype(entry.upperbound,entry.lowerbound)
    }

    /// Determine whether the right-hand side is a subtype of the
    /// left-hand side.  This is typically denoted as `lhs :> rhs`.
    fn is_subtype(&self, lhs: usize, rhs: usize) -> bool {
        if lhs == self.top { true }
        else if rhs == self.bottom { true }
        else {
            todo!("implement is_subtype");
        }
    }    

    /// Determine the _Greatest Lower Bound (GLB)_ between two types.
    fn greatest_lower_bound(&mut self, lhs: usize, rhs: usize) -> usize {
        if lhs == self.bottom { rhs }
        else if rhs == self.top { lhs }
        else {
            //
            todo!();
        }
    }
}
