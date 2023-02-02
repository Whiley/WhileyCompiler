#[derive(Clone,Debug,PartialEq)]
pub enum Types {
    /// Represents the type which is a _subtype_ of all other types in
    /// the type lattice.
    Bottom,
    /// Represents the type which is a _supertype_ of all other types
    /// in the type lattice.
    Top,
    /// Signed or unsigned integer with a given number of bits (upto
    /// `256`)
    Int{sign: bool, bits: u8},
    /// Represents a type which has no values (i.e. is uninhabited).
    Void
}

// =============================================================================
// Type
// =============================================================================

#[derive(Clone,Copy,Debug,PartialEq)]
pub struct Type(pub usize);
