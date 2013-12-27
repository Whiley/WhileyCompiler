import println from whiley.lang.System

(int,int) f(int x):
    return (x,x+2)

void ::main(System.Console sys):
    x = f(1)
    sys.out.println(Any.toString(x))
