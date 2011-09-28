import * from whiley.lang.*

void f({int} ls) requires no { i in ls | i <= 0}:
    debug str(ls)

void g({int} ls):
    f(ls)

void ::main(System sys,[string] args):
    g({0,1,2,3})
