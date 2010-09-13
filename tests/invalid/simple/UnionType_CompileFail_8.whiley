// This example is adapted from a recursive type example.
define ADD as 1
define SUB as 2
define MUL as 3
define DIV as 4
define bop as {int op, int lhs, int rhs} where op in {ADD,SUB,MUL,DIV}

void System::main([string] args):
    bop b = {op:0, lhs:1, rhs:2}
    print str(b)
