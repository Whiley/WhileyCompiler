import * from whiley.lang.*

string f({int} xs):
    return toString(xs)

string g({int} ys):
    return f(ys ∩ {1,2,3})

void ::main(System sys,[string] args):
    sys.out.println(g({1,2,3,4}))
    sys.out.println(g({2}))
    sys.out.println(g({}))
