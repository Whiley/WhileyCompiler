import println from whiley.lang.System

define func as int(int)

func g(int y):
    return &(int x -> x+y)

void ::main(System.Console sys):
    f = g(3)
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(3))
    f = g(19)
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(3))

