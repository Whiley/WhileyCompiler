import * from whiley.lang.*

void ::main(System sys,[string] args):
    xs = {1,2,3}
    zs = { x | x ∈ zs }
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(zs))
