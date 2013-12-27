import println from whiley.lang.System

// this is a comment!
string f({int} xs):
    return Any.toString(xs)

void ::main(System.Console sys):
    ys = {1,2,3}
    zs = ys
    sys.out.println(f(zs))
