import * from whiley.lang.*

void f({int} xs, {int} ys, {int} zs) requires zs == xs ∩ {1,2,3}:
    debug Any.toString(xs)

void h({int} ys, {int} zs):
    f(ys,zs,ys ∩ zs)

void ::main(System.Console sys):
    h({},{})
    h({1},{1,2})
    h({1,2,3},{3,4,5})
    h({1,2},{3,4,5})
