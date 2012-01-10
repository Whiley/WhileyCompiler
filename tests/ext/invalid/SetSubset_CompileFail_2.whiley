import * from whiley.lang.*

void f({real} x, {int} ys) requires x ⊆ ys:
    debug "X IS A SUBSET"

void ::main(System.Console sys,[string] args):
    f({1.0,2.0},{1,2,3})
    f({1.0,4.0},{1,2,3})
