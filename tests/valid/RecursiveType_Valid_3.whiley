import println from whiley.lang.System

// expression tree
define Expr as int | real |  // constant
    [Expr] |           // list constructor
    ListAccess         // list access

// list access
define ListAccess as { 
    Expr src, 
    Expr index
} 

define Value as int | real | [Value] | null

Value evaluate(Expr e):
    if e is real || e is int:
        return e
    else if e is [Expr]:
        r = []
        for i in e:
            r = r + [evaluate(i)]
        return r
    else:
        src = evaluate(e.src)
        index = evaluate(e.index)
        // santity checks
        if src is [Expr] && index is int && index >= 0 && index < |src|:
            return src[index]
        else:
            return null // stuck

public void ::main(System.Console sys):
    sys.out.println(Any.toString(evaluate(1)))
    sys.out.println(Any.toString(evaluate({src: [112,212,342], index:0})))
    sys.out.println(Any.toString(evaluate({src: [112312,289712,31231242], index:2})))
    sys.out.println(Any.toString(evaluate([1,2,3])))
