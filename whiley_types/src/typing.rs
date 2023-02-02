use syntactic_heap::SyntacticHeap;
use crate::*;

// ===================================================================
// Constraint
// ===================================================================

/// Represents a type constraint between one or more variables.
#[derive(Clone,Copy,Debug)]
pub enum Constraint {
    LowerBound(usize,Type)
}

impl Constraint {
    pub fn max_var(&self) -> usize {
        match self {
            Constraint::LowerBound(v,_) => *v,
        }
    }

    pub fn apply(&self, _types: &mut [Type]) {
        todo!("IMPLEMENT ME");
    }
}

// ===================================================================
// Typing
// ===================================================================

/// Represents a typing of all variables.
#[derive(Debug)]
pub struct Typing {
    /// Syntactic heap of types
    heap: SyntacticHeap<Types>,
    /// The set of constraints on each variable
    constraints: Vec<Constraint>,
    /// Current variable typings
    typing: Vec<Type>
}

impl Typing {
    const BOTTOM : Type = Type(0);

    /// Construct a new typing for a given number of variables.
    pub fn new(numvars: usize) -> Self {
        let mut heap = SyntacticHeap::new();
        let constraints = Vec::new();
        let typing = vec![Self::BOTTOM; numvars];
        // Bottom at index 0
        heap.push(Types::Bottom);
        //
        Self{heap, constraints, typing}
    }

    /// Add a give constraint to this typing.  This will update the
    /// typing, and that may result in the typing no longer being
    /// valid (i.e. one or more variables are mapping to `TOP`).
    pub fn constrain(&mut self, c: Constraint) -> bool {
        // Record constraint
        self.constraints.push(c);
        // Apply constraint
        c.apply(&mut self.typing);
        // Always true (for now)
        true
    }

    /// Insert a concrete type into this typing.  This may require
    /// allocating such a type on the heap, or it may reuse an
    /// existing (and matching) type.
    pub fn insert<T:Into<Types>>(&mut self, t: T) -> Type {
	// FIXME: this is where we want to manage the creation of
	// types carefully, such that we don't create any duplicate
	// types.  In particular, ideally, physical equality implies
	// semantic equality.
	//
        // Create new node
        let index = self.heap.push(t.into()).raw_index();
        // Done
        Type(index)
    }
}
