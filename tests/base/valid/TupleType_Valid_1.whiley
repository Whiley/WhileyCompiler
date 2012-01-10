import * from whiley.lang.*

(int,int) f(int x):
    return (x,x+2)

void ::main(System.Console sys,[string] args):
    (x,y) = f(1)
    sys.out.println(Any.toString(x))
    sys.out.println(Any.toString(y))
