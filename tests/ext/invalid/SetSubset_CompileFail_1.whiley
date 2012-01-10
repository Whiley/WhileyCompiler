import * from whiley.lang.*

void f({int} xs, {int} ys) requires xs ⊆ ys:
    debug "X IS A SUBSET"

void ::main(System.Console sys,[string] args):
    f({1,2},{1,2,3})
    f({1,4},{1,2,3})
