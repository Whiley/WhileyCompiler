import * from whiley.lang.*

void f({int} xs) requires xs ⊆ {1,2,3}:
    debug toString(xs)

void g({int} ys):
    f(ys ∩ {1,2,3,4})

void ::main(System sys,[string] args):
    g({1,2,3,4})
    g({2})
    g({})
