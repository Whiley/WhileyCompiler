import * from whiley.lang.*

void f({int} xs) requires |xs| > 0:
    debug toString(xs)

void g({int} ys, {int} zs):
    f(ys ∪ zs)

void ::main(System sys,[string] args):
    g({},{1})
    g({2},{2})
    g({},{})
