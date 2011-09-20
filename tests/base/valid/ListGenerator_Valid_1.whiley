import * from whiley.lang.*

void ::f(System sys, [int] x):
    z = |x|
    sys.out.println(str(z))
    sys.out.println(str(x[z-1]))

void ::main(System sys,[string] args):
     arr = [1,2,3]
     // following line should block
     f(sys,arr)
