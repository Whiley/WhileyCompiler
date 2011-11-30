import * from whiley.lang.*

void f([int] x) requires |x| > 0:
    z = |x|
    debug Any.toString(z) + "\n"
    debug Any.toString(x[z-1]) + "\n"

void ::main(System sys,[string] args):
     arr = [1,2,3]
     f(arr)
