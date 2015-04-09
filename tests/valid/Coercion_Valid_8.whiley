import whiley.lang.*

type Expr is real | [Expr]

function f(Expr x) -> real:
    if x is [Expr]:
        return (real) |x|
    else:
        return x

method main(System.Console sys) -> void:
    assume f([1.0, 2.0, 3.0]) == 3.0
    assume f(1.234) == 1.234
