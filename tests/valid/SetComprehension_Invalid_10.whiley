import println from whiley.lang.System

// this is a comment!
string f({int} xs):
    return Any.toString(xs)

void ::main(System.Console sys):
    ys = {1,2,3}
    zs = {z | z in ys, z > 1}
    sys.out.println(f(zs))
