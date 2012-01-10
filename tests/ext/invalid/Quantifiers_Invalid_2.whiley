import * from whiley.lang.*

void f({int} ls) requires no { i in ls | i <= 0}:
    debug Any.toString(ls)

void g({int} ls):
    f(ls)

void ::main(System.Console sys):
    g({0,1,2,3})
