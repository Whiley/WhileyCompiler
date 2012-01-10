import * from whiley.lang.*

// This example is adapted from a recursive type example.
define ADD as 1
define SUB as 2
define MUL as 3
define DIV as 4
define bop as {int op, int lhs, int rhs} where op in {ADD,SUB,MUL,DIV}

bop f(bop b):
    return b

void ::main(System.Console sys):
    b = {op:0, lhs:1, rhs:2}
    debug Any.toString(f(b))
