import println from whiley.lang.System

define ADD as 0
define SUB as 1
define MUL as 2
define DIV as 3

// binary operation
define BOp as { ADD, SUB, MUL, DIV }
define BinOp as { BOp op, Expr lhs, Expr rhs } 

// list access
define ListAccess as { 
    Expr src, 
    Expr index
} 

// expression tree
define Expr as int |  // constant
    BinOp |            // binary operator
    [Expr] |           // list constructor
    ListAccess         // list access

int f(Expr e):
    if e is int:
        return e
    else if e is [int]:
        return |e|
    else:
        return 1

void ::main(System.Console sys):
    sys.out.println(f(1))
    sys.out.println(f([1,2,3]))
    sys.out.println(f({op: ADD, lhs: 1, rhs: 2}))
