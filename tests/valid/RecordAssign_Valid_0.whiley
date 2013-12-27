import println from whiley.lang.System

define tac1tup as {int f1, int f2} where f1 < f2

tac1tup f():
    return {f1:1,f2:3}

void ::main(System.Console sys):
    x = f()    
    x.f1 = x.f2 - 2
    assert x.f1 != x.f2
    sys.out.println(Any.toString(x))
