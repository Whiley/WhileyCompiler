import println from whiley.lang.System

(int,int) f(real z):
    x,y = z
    return (x,y)

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(10.0/5)))
    sys.out.println(Any.toString(f(10.0/4)))
    sys.out.println(Any.toString(f(1.0/4)))
    sys.out.println(Any.toString(f(103.0/2)))
    sys.out.println(Any.toString(f(-10.0/5)))
    sys.out.println(Any.toString(f(-10.0/4)))
    sys.out.println(Any.toString(f(-1.0/4)))
    sys.out.println(Any.toString(f(-103.0/2)))
    sys.out.println(Any.toString(f(-10.0/-5)))
    sys.out.println(Any.toString(f(-10.0/-4)))
    sys.out.println(Any.toString(f(-1.0/-4)))
    sys.out.println(Any.toString(f(-103.0/-2)))
    sys.out.println(Any.toString(f(10.0/-5)))
    sys.out.println(Any.toString(f(10.0/-4)))
    sys.out.println(Any.toString(f(1.0/-4)))
    sys.out.println(Any.toString(f(103.0/-2)))
