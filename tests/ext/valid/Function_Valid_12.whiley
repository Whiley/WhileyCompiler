import * from whiley.lang.*

int f(int x) ensures $ > x:
    return x+1


int g(int x, int y) requires x > y:
    return x+y

void ::main(System.Console sys,[string] args):
    a = 2
    b = 1
    if |args| == 0:
        a = f(b)
    x = g(a,b)
    sys.out.println(Any.toString(x))
