import println from whiley.lang.System

// this is a comment!
string f({int} xs) requires |xs| > 0:
    return Any.toString(xs)

void ::main(System.Console sys):
    ys = {1,2,3}
    zs = ys
    sys.out.println(f(zs))
