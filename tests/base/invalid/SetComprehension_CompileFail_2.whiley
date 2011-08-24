import whiley.lang.*:*

void ::main(System sys,[string] args):
    xs = 1
    zs = { x | y ∈ xs }
    sys.out.println(str(xs))
    sys.out.println(str(zs))
