import * from whiley.lang.*

string f({int} xs):
    if |xs| > 0:
        return Any.toString(xs)
    else:
        return "FAILED"

string g({int} ys):
    return f(ys ∪ {1})

void ::main(System.Console sys,[string] args):
    sys.out.println(g({}))
    sys.out.println(g({2}))
