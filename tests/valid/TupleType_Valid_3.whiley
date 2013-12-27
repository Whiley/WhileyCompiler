import println from whiley.lang.System

define Tup1 as (int, int)
define Tup2 as (real, real)

Tup2 f(Tup1 x):
    return x

void ::main(System.Console sys):
    x = f((1,2))
    sys.out.println(Any.toString(x))
