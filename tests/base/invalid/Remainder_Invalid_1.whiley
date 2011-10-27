import * from whiley.lang.*

int f(real x, int y):
    return x % y    

void ::main(System sys,[string] args):
    sys.out.println(toString(f(10.23,5)))
    sys.out.println(toString(f(10.23,4)))
    sys.out.println(toString(f(1,4)))
    sys.out.println(toString(f(10.233,2)))
    sys.out.println(toString(f(-10.23,5)))
    sys.out.println(toString(f(-10.23,4)))
    sys.out.println(toString(f(-1,4)))
    sys.out.println(toString(f(-10.233,2)))
    sys.out.println(toString(f(-10.23,-5)))
    sys.out.println(toString(f(-10.23,-4)))
    sys.out.println(toString(f(-1,-4)))
    sys.out.println(toString(f(-10.233,-2)))
    sys.out.println(toString(f(10.23,-5)))
    sys.out.println(toString(f(10.23,-4)))
    sys.out.println(toString(f(1,-4)))
    sys.out.println(toString(f(10.233,-2)))
