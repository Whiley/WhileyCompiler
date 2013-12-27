import println from whiley.lang.System

define func as int(int)

func g():
    return &(int x -> x+1)

void ::main(System.Console sys):
    f = g()
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(3))
