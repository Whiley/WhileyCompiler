import * from whiley.lang.*

void ::main(System sys,[string] args):
    xs = {1,2,3}
    zs = { x | x ∈ zs }
    sys.out.println(str(xs))
    sys.out.println(str(zs))
