type Expr is int | Expr[]

function f(Expr x) -> int:
    if x is Expr[]:
        return |x|
    else:
        return x

public export method test() :
    assume f([1, 2, 3]) == 3
    assume f(1) == 1
