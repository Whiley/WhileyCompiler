import println from whiley.lang.System

{int} f([int] xs):
    return { x | x in xs, x > 1 }

void ::main(System.Console sys):
    sys.out.println(Any.toString(f([1,2,3])))
    sys.out.println(Any.toString(f([1,2,3,3])))
    sys.out.println(Any.toString(f([-1,1,2,-1,3,3])))
