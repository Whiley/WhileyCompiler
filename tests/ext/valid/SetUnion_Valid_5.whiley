import * from whiley.lang.*

string f({int} xs) requires |xs| > 0:
    return toString(xs)

string g({int} ys):
    return f(ys ∪ {1})

void ::main(System sys,[string] args):
    sys.out.println(g({}))
    sys.out.println(g({2}))
