import whiley.lang.System

type plist is ([int] xs) where |xs| > 0 && xs[0] == 0

type expr is [int] | int

type tup is {int p, expr lhs}

function f(tup t) => string:
    if (t.lhs is plist) && ((|t.lhs| > 0) && (t.lhs[0] == 0)):
        return "MATCH" ++ Any.toString(t.lhs)
    else:
        return "NO MATCH"

method main(System.Console sys) => void:
    sys.out.println(f({p: 0, lhs: [0]}))
    sys.out.println(f({p: 0, lhs: [0, 1]}))
    sys.out.println(f({p: 0, lhs: [1, 1]}))
