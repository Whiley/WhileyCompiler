import * from whiley.lang.*

int f({int} xs):
    return |xs|

void ::main(System sys,[string] args):
    ys = {1.0234234,1.12}
    xs = {1,2,3,4}
    f(xs ∪ ys)
    sys.out.println(str(xs))
