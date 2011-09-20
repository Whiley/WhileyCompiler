import * from whiley.lang.*

real diver(real x, real y, real z):
    return x / y / z

void ::main(System sys,[string] args):
    sys.out.println(str(diver(1.2,3.4,4.5)))
    sys.out.println(str(diver(1000,300,400)))
