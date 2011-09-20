import * from whiley.lang.*

void f([int] x) requires |x| > 0:
    z = |x|
    debug str(z) + "\n"
    debug str(x[z-1]) + "\n"

void ::main(System sys,[string] args):
     arr = [1,2,3]
     f(arr)
