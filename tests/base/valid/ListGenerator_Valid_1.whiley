import * from whiley.lang.*

void ::f(System sys, [int] x):
    z = |x|
    sys.out.println(toString(z))
    sys.out.println(toString(x[z-1]))

void ::main(System sys,[string] args):
     arr = [1,2,3]
     // following line should block
     f(sys,arr)
