import println from whiley.lang.System

int f1(int x):
    return x + 1

int f2(int x):
    return x * 2

int g(int(int) func):
    return func(1234)
    
void ::main(System.Console sys):
    sys.out.println(Any.toString(g(&f1)))
    sys.out.println(Any.toString(g(&f2)))
