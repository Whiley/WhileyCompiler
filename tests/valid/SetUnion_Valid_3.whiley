import println from whiley.lang.System

void ::main(System.Console sys):
    xs = {1,2,3}
    ys = [2,3,4]
    zs = xs ∪ ys
    sys.out.println(Any.toString(zs))
