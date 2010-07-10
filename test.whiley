// The current parser state
define state as (string input, int pos) where pos >= 0 && pos <= |input|

// A simple, recursive expression tree
define expr as (int num) | (int op, expr lhs) | (string err)

// Top-level parse method
expr parse(string input):
    (expr e, state st) r = parseAddSubExpr((input:input,pos:0))
    return r.e

(expr e, state st) parseAddSubExpr(state st):    
    return (e:(num:1),st:st)
