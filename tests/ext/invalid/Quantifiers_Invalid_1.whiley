import * from whiley.lang.*

void f({int} ls) requires some { i in ls | i < 0}:
    debug toString(ls)

void g({int} ls):
    f(ls)

void ::main(System sys,[string] args):
    g({1,2,3})
