use syntactic_heap::{Ref,SyntacticHeap};
use crate::solution::{Solution,Var};
use crate::*;

// ===================================================================
// Constraint
// ===================================================================

/// Represents a subtype constraint between two variables.
#[derive(Clone,Copy,Debug)]
struct SubtypeConstraint {
    supvar: Var,
    subvar: Var
}
    
// ===================================================================
// Typing
// ===================================================================

/// Represents a typing of all variables.
#[derive(Debug)]
pub struct Typing<T> {
    /// Syntactic heap of types
    heap: SyntacticHeap<T>,
    /// The set of constraints on each variable
    constraints: Vec<SubtypeConstraint>,
    /// Candidate solutions for each variable.
    solution: Solution<T>
}

impl<T:Copy+Bottom+Top> Typing<T> {
    const TOP : usize = 0;
    const BOTTOM : usize = 1;    

    /// Construct a new typing for a given number of variables.
    pub fn new() -> Self {
        let mut heap = SyntacticHeap::new();
        let constraints = Vec::new();
        // Top at index 0
        heap.push(T::TOP);
        // Bottom at index 1
        heap.push(T::BOTTOM);        
        //
        let solution = Solution::new(Self::TOP,Self::BOTTOM);        
        //
        Self{heap, constraints, solution}
    }

    /// Check whether this typing is currently _satisfiable_ or not.
    /// More specifically, that is whether or not any variable has
    /// been given the special `TOP` type.
    pub fn ok(&self) -> bool {
        self.solution.ok()
    }

    /// Allocate a fresh variable initialised with `BOTTOM`.
    pub fn fresh_var(&mut self) -> Var {
        self.solution.fresh_var()
    }
    
    /// Constrain a given variable to be _greater or equal_ to a given
    /// bound (i.e. it must be a _supertype_ of the bound).  This is
    /// typically denoted as `v :> T` where `v` is the variable being
    /// constrained, and `T` is a concrete type (i.e. the bound).  For
    /// example, given a variable `v` and constraints `v :> int` and
    /// `v :> bool` then a valid type for `v` might be `int|bool`.
    /// Note that adding a constraint can result in the variable
    /// becoming _unsatisfiable_ (i.e. no valid type exists which
    /// meets all constraints).
    pub fn constrain_above(&mut self, supvar: Var, bound: T) -> bool {
        // Insert bound into the heap
        let bound_index = self.insert(bound);
        // Create heap reference
        let bound_ref = Ref::new(&self.heap,bound_index);
        //
        self.solution.constrain_above(supvar,bound_ref)
    }
        
    /// Insert a concrete type into this typing.  This may require
    /// allocating such a type on the heap, or it may reuse an
    /// existing (and matching) type.
    fn insert(&mut self, t: T) -> usize {
        // FIXME: this is where we want to manage the creation of
        // types carefully, such that we don't create any duplicate
        // types.  In particular, ideally, physical equality implies
        // semantic equality.
        //
        // Create new node
        self.heap.push(t).raw_index()        
    }
}
