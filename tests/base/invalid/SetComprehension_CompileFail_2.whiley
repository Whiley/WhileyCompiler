import * from whiley.lang.*

void ::main(System.Console sys,[string] args):
    xs = 1
    zs = { x | y ∈ xs }
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(zs))
