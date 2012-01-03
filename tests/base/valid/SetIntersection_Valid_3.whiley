import * from whiley.lang.*

string f({int} xs):
    return Any.toString(xs)

string g({int} ys):
    return f(ys ∩ {1,2})

void ::main(System sys,[string] args):
    sys.out.println(g({}))
    sys.out.println(g({2,3,4,5,6}))
    sys.out.println(g({2,6}))
