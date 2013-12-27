import println from whiley.lang.System

int f(int x) ensures $ > x:
    x = x + 1
    return x

void ::main(System.Console sys):
    y = f(1)
    sys.out.println(Any.toString(y))
    
