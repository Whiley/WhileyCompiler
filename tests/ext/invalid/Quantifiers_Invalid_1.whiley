import * from whiley.lang.*

void f({int} ls) requires some { i in ls | i < 0}:
    debug Any.toString(ls)

void g({int} ls):
    f(ls)

void ::main(System.Console sys):
    g({1,2,3})
