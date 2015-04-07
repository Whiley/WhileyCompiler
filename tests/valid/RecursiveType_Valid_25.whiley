import whiley.lang.*

type Expr is real | [Expr]

type Value is real | [Value]

function init() -> Value:
    return 0.0123

method main(System.Console sys) -> void:
    Value v = init()
    if v is [Expr]:
        sys.out.println_s("GOT LIST")
    else:
        sys.out.println(v)
