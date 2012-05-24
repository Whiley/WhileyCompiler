import println from whiley.lang.System

// ====================================================
// A simple calculator for expressions
// ====================================================

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

// ====================================================
// Expression Evaluator
// ====================================================

int evaluate(Expr e):
    if e is int:
        return e
    else if e is BinOp:
        return 2
    else if e is [Expr]:
        return 3
    else if e is ListAccess:
        src = evaluate(e.src)
        index = evaluate(e.index)
        // santity checks
        return src + index
    else:
        return -1 // deadcode

void ::main(System.Console sys):
    e = 1
    sys.out.println(evaluate(e))
    e = {op: ADD, lhs: e, rhs: e}
    sys.out.println(evaluate(e))
    e = [e]
    sys.out.println(evaluate(e))
    e = {src: e, index: 1}
    sys.out.println(evaluate(e))
