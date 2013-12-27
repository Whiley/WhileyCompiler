import println from whiley.lang.System

define Expr as real | Var | BinOp
define BinOp as { Expr lhs, Expr rhs } 
define Var as { string id }

define SyntaxError as { string err }
define SExpr as SyntaxError | Expr

Expr build(int i):    
    if i > 10:
        return { id: "var" }
    else if i > 0:
        return i
    else:
        return { lhs:build(i+10), rhs:build(i+1) } 

SExpr sbuild(int i):
    if i > 20:
        return { err: "error" }
    else:
        return build(i)

real evaluate(Expr e):
    if e is real:
        return e
    if e is {string id}:
        return |e.id|
    else:
        return evaluate(e.lhs) + evaluate(e.rhs)

// Main method
public void ::main(System.Console sys):
    i = -5
    while i < 10:
        e = sbuild(i)
        if e is { string err}:
            sys.out.println("syntax error: " + e.err)
        else:
            e = evaluate(e)
            sys.out.println(Any.toString(e))
        i = i + 1
