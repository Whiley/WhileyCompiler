import whiley.lang.*:*

(int,int) f(real z):
    x,y = z
    return (x,y)

void ::main(System sys,[string] args):
    sys.out.println(str(f(10.0/5)))
    sys.out.println(str(f(10.0/4)))
    sys.out.println(str(f(1.0/4)))
    sys.out.println(str(f(103.0/2)))
    sys.out.println(str(f(-10.0/5)))
    sys.out.println(str(f(-10.0/4)))
    sys.out.println(str(f(-1.0/4)))
    sys.out.println(str(f(-103.0/2)))
    sys.out.println(str(f(-10.0/-5)))
    sys.out.println(str(f(-10.0/-4)))
    sys.out.println(str(f(-1.0/-4)))
    sys.out.println(str(f(-103.0/-2)))
    sys.out.println(str(f(10.0/-5)))
    sys.out.println(str(f(10.0/-4)))
    sys.out.println(str(f(1.0/-4)))
    sys.out.println(str(f(103.0/-2)))
