import println from whiley.lang.System

define tac2ta as {int f1, int f2}
define tac2tb as {int f1, int f2}

tac2tb f(tac2ta x):
    return {f1: x.f1-1, f2: x.f2}

void ::main(System.Console sys):
    x = {f1:2,f2:3}
    sys.out.println(Any.toString(x))
    x.f1 = 1
    y = f(x)
    sys.out.println(Any.toString(y))
