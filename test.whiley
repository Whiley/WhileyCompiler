// The current parser state
define state as (string input, int pos) where pos >= 0 && pos <= |input|

// Indicates a parser state which is non-empty
define nonEmptyState as state where $.pos < |$.input|

// A simple, recursive expression tree
define ident as (string id)
define expr as (int num) | ident | (int op, expr lhs, expr rhs) | (string err)

(ident e, state st) parseIdentifier(state st):    
    return (e:"_",st:st)
