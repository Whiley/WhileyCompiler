import println from whiley.lang.System

int f([int] x) requires |x| > 0:
    z = |x|
    debug Any.toString(z) + "\n"
    debug Any.toString(x[z-1]) + "\n"
    return z

void ::main(System.Console sys):
     arr = [1,2,3]
     sys.out.println(f(arr))
