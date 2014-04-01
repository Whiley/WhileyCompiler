import whiley.lang.System

type Expr is real | [Expr]

type Value is real | [Value]

function init() => Value:
    return 0.0123

method main(System.Console sys) => void:
    Value v = init()
    if v is [Expr]:
        sys.out.println("GOT LIST")
    else:
        sys.out.println(Any.toString(v))
