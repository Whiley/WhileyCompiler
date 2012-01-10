import * from whiley.lang.*

void ::main(System.Console sys,[string] args):
    xs = {1,2,3}
    ys = [2,3,4]
    zs = xs ∩ ys
    sys.out.println(Any.toString(zs))
