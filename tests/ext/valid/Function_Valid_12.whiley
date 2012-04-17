import println from whiley.lang.System

int f(int x) ensures $ > x:
    return x+1


int g(int x, int y) requires x > y:
    return x+y

void ::main(System.Console sys):
    a = 2
    b = 1
    if |sys.args| == 0:
        a = f(b)
    x = g(a,b)
    sys.out.println(Any.toString(x))
