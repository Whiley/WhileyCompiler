import * from whiley.lang.*

void f({int} xs, {int} ys, {int} zs) requires zs == xs ∪ ys:
    debug str(xs)

void g({int} ys):
    f(ys,ys,ys ∪ {6})

void h({int} ys, {int} zs):
    f(ys,zs,ys ∪ zs)

void ::main(System sys,[string] args):
    g({})
    g({2})
    g({1,2,3})
    h({},{})
    h({1},{2})
    h({1,2,3},{3,4,5})
