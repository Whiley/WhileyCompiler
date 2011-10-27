import * from whiley.lang.*

void ::main(System sys,[string] args):
    xs = {1,2,3}
    zs = { x+y | x ∈ xs }
    sys.out.println(toString(xs))
    sys.out.println(toString(zs))
