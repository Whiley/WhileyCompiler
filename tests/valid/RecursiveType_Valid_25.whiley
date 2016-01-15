

type Expr is real | Expr[]

type Value is real | Value[]

function init() -> Value:
    return 0.0123

public export method test() :
    Value v = init()
    if v is Expr[]:
        assume false
    else:
        assume v == 0.0123
