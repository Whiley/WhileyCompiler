// ====================================================
// A simple, recursive expression tree
// ====================================================

define ADD as 0
define SUB as 1
define MUL as 2
define DIV as 3

// binary operation
define BOp as { ADD, SUB, MUL, DIV }
define BinOp as { BOp op, Expr lhs, Expr rhs } 

// variables
define Var as { string id }

// list access
define ListAccess as { 
    Expr src, 
    Expr index
} 

// expression tree
define Expr as real |  // constant
    Var |              // variable
    BinOp |            // binary operator
    [Expr] |           // list constructor
    ListAccess         // list access
 
// values
define value as real | [value] | null

// ====================================================
// Expression Evaluator
// ====================================================

value evaluate(Expr e):
    if e ~= int:
        return e
    else if e ~= Var:
        return 0 // temporary hack for now
    else if e ~= BinOp:
        lhs = evaluate(e.lhs)
        rhs = evaluate(e.rhs)
        // check if stuck
        if !(lhs ~= int && rhs ~= int):
            return null 
        // switch statement would be good
        if e.op == ADD:
            return lhs + rhs
        else if e.op == SUB:
            return lhs - rhs
        else if e.op == MUL:
            return lhs * rhs
        else if rhs != 0:
            return lhs / rhs
        return null // divide by zero
    else if e ~= ListAccess:
        src = evaluate(e.src)
        index = evaluate(e.index)
        // santity checks
        if src ~= [Expr] && index ~= int &&
            index >= 0 && index < |src|:
            return src[index]
        else:
            return null // stuck
    else:
        // e must be a list expression
        return 0
