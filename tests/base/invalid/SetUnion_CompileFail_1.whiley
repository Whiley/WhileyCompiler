import * from whiley.lang.*

void ::main(System sys,[string] args):
    y = 1.0234234
    xs = {1,2,3,4}
    xs = xs ∪ y
    sys.out.println(toString(xs))
