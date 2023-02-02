use syntactic_heap::SyntacticHeap;
use crate::*;

// ===================================================================
// Constraint
// ===================================================================

/// Represents a type constraint between one or more variables.
#[derive(Clone,Copy,Debug)]
pub enum Constraint<T> {
    /// Constrains a given variable to have a given _lower_ bound.
    /// For example, we might say `v :> int` to indicate that variable
    /// `v` must be able to accept an `int` value.
    LowerBound(usize,T),
    /// Constrains a given variable to have a given _upper_ bound.
    /// For example, we might say `int :> v` to indicate that variable
    /// `v` must be able to flow into an `int` value.
    UpperBound(T,usize),
    /// Constraints the right variable to be a _subtype_ of the left
    /// variable (i.e. this descrives `lhs :> rhs`).
    Subtype(usize,usize)
}

/// Represents a type constraint between one or more variables.
impl<T> Constraint<T> {
    fn apply(&self, _typing: &mut [usize]) -> bool {
        todo!("GOT HERE");
    }
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
    constraints: Vec<Constraint<T>>,
    /// Current variable typings (which are references into the
    /// syntactic heap).
    typing: Vec<usize>
}

impl<T:Copy+Bottom> Typing<T> {
    const BOTTOM : usize = 0;

    /// Construct a new typing for a given number of variables.
    pub fn new(numvars: usize) -> Self {
        let mut heap = SyntacticHeap::new();
        let constraints = Vec::new();
        let typing = vec![Self::BOTTOM; numvars];
        // Bottom at index 0
        heap.push(T::BOTTOM);
        //
        Self{heap, constraints, typing}
    }

    /// Check whether this typing is currently _satisfiable_ or not.
    pub fn ok(&self) -> bool {
        false
    }
    
    /// Add a give constraint to this typing.  This will update the
    /// typing, and that may result in the typing no longer being
    /// valid (i.e. one or more variables are mapping to `TOP`).
    pub fn constrain(&mut self, c: Constraint<T>) -> bool {
        // Record constraint
        self.constraints.push(c);
        // Apply constraint
        c.apply(&mut self.typing)
    }

    /// Insert a concrete type into this typing.  This may require
    /// allocating such a type on the heap, or it may reuse an
    /// existing (and matching) type.
    pub fn insert(&mut self, t: T) -> usize {
        // FIXME: this is where we want to manage the creation of
        // types carefully, such that we don't create any duplicate
        // types.  In particular, ideally, physical equality implies
        // semantic equality.
        //
        // Create new node
        self.heap.push(t).raw_index()        
    }
}
