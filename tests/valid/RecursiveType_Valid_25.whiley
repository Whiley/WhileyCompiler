

type Expr is bool | Expr[]

type Value is bool | Value[]

function init() -> Value:
    return false

public export method test() :
    Value v = init()
    if v is Expr[]:
        assume false
    else:
        assume v == false
