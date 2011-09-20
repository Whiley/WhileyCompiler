import * from whiley.lang.*

// this is a comment!
string f({int} xs):
    return str(xs)

void ::main(System sys,[string] args):
    ys = {1,2,3}
    zs = ys
    sys.out.println(f(zs))
