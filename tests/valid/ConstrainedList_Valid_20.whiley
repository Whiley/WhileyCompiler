import println from whiley.lang.System

define wierd as [int] where some {x in $ | x > 0}

wierd f([int] xs) requires |xs| > 0:
    xs[0] = 1
    return xs

void ::main(System.Console sys):
    rs = f([-1,-2])
    sys.out.println(Any.toString(rs))





