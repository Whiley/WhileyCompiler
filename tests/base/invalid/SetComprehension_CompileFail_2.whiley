import * from whiley.lang.*

void ::main(System sys,[string] args):
    xs = 1
    zs = { x | y ∈ xs }
    sys.out.println(toString(xs))
    sys.out.println(toString(zs))
