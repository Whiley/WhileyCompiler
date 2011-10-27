import * from whiley.lang.*

void f({int} ls) requires some { i in ls | i < 0}:
    debug toString(ls)

void ::main(System sys,[string] args):
    f({1,2,3})
