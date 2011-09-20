import * from whiley.lang.*

int f(real x, real y):
    return x % y    

void ::main(System sys,[string] args):
    sys.out.println(str(f(10.5343,5.2354)))
    sys.out.println(str(f(10.5343,4.2345)))
    sys.out.println(str(f(1,4.2345)))
    sys.out.println(str(f(10.53433,2)))
    sys.out.println(str(f(-10.5343,5.2354)))
    sys.out.println(str(f(-10.5343,4.2345)))
    sys.out.println(str(f(-1,4.2345)))
    sys.out.println(str(f(-10.53433,2)))
    sys.out.println(str(f(-10.5343,-5)))
    sys.out.println(str(f(-10.5343,-4)))
    sys.out.println(str(f(-1,-4)))
    sys.out.println(str(f(-10.53433,-2)))
    sys.out.println(str(f(10.5343,-5)))
    sys.out.println(str(f(10.5343,-4)))
    sys.out.println(str(f(1,-4)))
    sys.out.println(str(f(10.53433,-2)))
