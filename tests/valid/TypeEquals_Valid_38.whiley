import whiley.lang.System

type expr is [int] | int

type tup is {int p, expr lhs}

function f(tup t) => string:
    if (t.lhs is [int]) && ((|t.lhs| > 0) && (t.lhs[0] == 0)):
        return "MATCH" ++ Any.toString(t.lhs)
    else:
        return "NO MATCH"

method main(System.Console sys) => void:
    sys.out.println(f({p: 0, lhs: [0]}))
    sys.out.println(f({p: 0, lhs: [1]}))
    sys.out.println(f({p: 0, lhs: []}))
