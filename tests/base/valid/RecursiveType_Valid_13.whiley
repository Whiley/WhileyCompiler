import println from whiley.lang.System

define Expr as real | [Expr]
define Value as real | [Value]

Value init():
    return 0.0123

void ::main(System.Console sys):
    v = init()
    if v is [Expr]:
        sys.out.println("GOT LIST")
    else:
        sys.out.println(Any.toString(v))
