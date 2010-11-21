// ====================================================
// A simple, recursive expression tree
// ====================================================

define ADD as 0
define SUB as 1
define MUL as 2
define DIV as 3

// binary operation
define bop as { ADD, SUB, MUL, DIV }
define binop as { bop op, expr lhs, expr rhs } 

// variables
define var as { string id }

// list access
define ListAccess as { 
    expr src, 
    expr index
} 

// expression tree
define expr as real |  // constant
    var |              // variable
    binop |            // binary operator
    [expr] |           // list constructor
    ListAccess         // list access
 
// values
define value as real | [value] | null

// ====================================================
// Expression Evaluator
// ====================================================

value evaluate(expr e):
    if e ~= int:
        return e
    else if e ~= var:
        return 0 // temporary hack for now
    else if e ~= binop:
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
        if src ~= [expr] && index ~= int &&
            index >= 0 && index < |src|:
            return src[index]
        else:
            return null // stuck
    else:
        // e must be a list expression
        return 0
