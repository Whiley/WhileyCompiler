// ===================================================================
// Bottom
// ===================================================================

/// Defines an abstract value which has a specific `BOTTOM` value in
/// the lattice.
pub trait Bottom {
    const BOTTOM: Self;
}

pub trait IsBottom {
    /// Check whether `self` is the bottom value or not.
    fn is_bottom(&self) -> bool;
}

impl<T: PartialEq + Bottom> IsBottom for T {
    fn is_bottom(&self) -> bool {
        self == &T::BOTTOM
    }
}

// ===================================================================
// Top
// ===================================================================

/// Defines an abstract value which has a specific `TOP` value in the
/// lattice.
pub trait Top {
    const TOP: Self;
}

pub trait IsTop {
    /// Check whether `self` is the top value or not.
    fn is_top(&self) -> bool;
}

impl<T: PartialEq + Top> IsTop for T {
    fn is_top(&self) -> bool {
        self == &T::TOP
    }
}
