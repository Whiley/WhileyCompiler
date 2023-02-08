use syntactic_heap::SyntacticHeap;
use crate::*;

// ===================================================================
// Constraint Var
// ===================================================================

#[derive(Clone,Copy,Debug)]
pub struct Var(usize);

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
    /// Current variable typings (which are references into the
    /// syntactic heap).
    typing: Vec<usize>
}

impl<T:Copy+Bottom+Top> Typing<T> {
    const BOTTOM : usize = 0;
    const TOP : usize = 0;    

    /// Construct a new typing for a given number of variables.
    pub fn new() -> Self {
        let mut heap = SyntacticHeap::new();
        let constraints = Vec::new();
        let typing = Vec::new();
        // Bottom at index 0
        heap.push(T::BOTTOM);
        // Top at index 1
        heap.push(T::TOP);        
        //
        Self{heap, constraints, typing}
    }

    /// Check whether this typing is currently _satisfiable_ or not.
    /// More specifically, that is whether or not any variable has
    /// been given the special `TOP` type.
    pub fn ok(&self) -> bool {
        for i in 0 .. self.typing.len() {
            if self.typing[i] == Self::TOP {
                return false;
            }
        }
        // All good
        true
    }

    /// Get type currently associated with a given variable.
    pub fn get(&self, var: Var) -> &T {
        // Determine heap index
        let index = self.typing[var.0];
        // Get reference
        self.heap.get(index)
    }

    /// Allocate a fresh variable initialised with `BOTTOM`.
    pub fn fresh_var(&mut self) -> Var {
        // Determine index of new variable
        let index = self.typing.len();
        // Initialise new variable
        self.typing.push(Self::BOTTOM);
        // Done
        Var(index)
    }
    
    /// Add a lower bound constraint to this typing.  This will update the
    /// typing, and that may result in the typing no longer being
    /// valid (i.e. one or more variables are mapping to `TOP`).
    pub fn constrain_above(&mut self, supvar: Var, bound: T) -> bool {
        // Insert bound into the heap
        let bound_index = self.insert(bound);
        // Get variable's current typg
        let var_index = self.typing[supvar.0];
        // Determine glb
        let glb_index = self.greatest_lower_bound(var_index,bound_index);
        //
        if glb_index != var_index {
            // Changed!
            self.typing[supvar.0] = glb_index;
        }
        //
        glb_index != Self::TOP
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

    /// Determine the _Greatest Lower Bound (GLB)_ between two types.
    fn greatest_lower_bound(&mut self, lhs: usize, rhs: usize) -> usize {
        if lhs == Self::BOTTOM { rhs }
        else if rhs == Self::BOTTOM { lhs }
        else {
            //
            todo!();
        }
    }
}
