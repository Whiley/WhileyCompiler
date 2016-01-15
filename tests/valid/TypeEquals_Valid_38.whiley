

type expr is int[] | int

type tup is {int p, expr lhs}

function f(tup t) -> bool:
    if (t.lhs is int[]) && ((|t.lhs| > 0) && (t.lhs[0] == 0)):
        return true
    else:
        return false

public export method test() :
    assume f({p: 0, lhs: [0]}) == true
    assume f({p: 0, lhs: [1]}) == false
    assume f({p: 0, lhs: [0;0]}) == false
