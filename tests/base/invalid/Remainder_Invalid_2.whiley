import * from whiley.lang.*

int f(int x, real y):
    return x % y    

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(10,5.23)))
    sys.out.println(Any.toString(f(10,4)))
    sys.out.println(Any.toString(f(1,4)))
    sys.out.println(Any.toString(f(103,2)))
    sys.out.println(Any.toString(f(-10,5.23)))
    sys.out.println(Any.toString(f(-10,4)))
    sys.out.println(Any.toString(f(-1,4)))
    sys.out.println(Any.toString(f(-103,2)))
    sys.out.println(Any.toString(f(-10,-5.23)))
    sys.out.println(Any.toString(f(-10,-4)))
    sys.out.println(Any.toString(f(-1,-4)))
    sys.out.println(Any.toString(f(-103,-2)))
    sys.out.println(Any.toString(f(10,-5.23)))
    sys.out.println(Any.toString(f(10,-4)))
    sys.out.println(Any.toString(f(1,-4)))
    sys.out.println(Any.toString(f(103,-2)))
