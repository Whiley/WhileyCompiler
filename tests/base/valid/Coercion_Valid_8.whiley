import println from whiley.lang.System

define Expr as real | [Expr]

real f(Expr x):
    if x is [Expr]:
        return |x|
    else:
        return x

void ::main(System.Console sys):
    sys.out.println(Any.toString(f([1,2,3])))
    sys.out.println(Any.toString(f([1.0,2.0,3.0])))
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(1.234)))
