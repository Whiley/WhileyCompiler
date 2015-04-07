import whiley.lang.*

type Expr is real | [Expr]

function f(Expr x) -> real:
    if x is [Expr]:
        return |x|
    else:
        return x

method main(System.Console sys) -> void:
    sys.out.println(f([1, 2, 3]))
    sys.out.println(f([1.0, 2.0, 3.0]))
    sys.out.println(f(1))
    sys.out.println(f(1.234))
