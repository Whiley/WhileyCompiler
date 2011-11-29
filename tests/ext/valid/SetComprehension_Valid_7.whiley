import * from whiley.lang.*

// this is a comment!
string f({int} xs) requires |xs| > 0:
    return toString(xs)

void ::main(System sys,[string] args):
    ys = {1,2,3}
    zs = {z | z in ys, z > 1}
    sys.out.println(f(zs))
