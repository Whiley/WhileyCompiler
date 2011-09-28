import * from whiley.lang.*

void f({int} ls) requires no { i in ls | i <= 0}:
    debug str(ls)

void ::main(System sys,[string] args):
    f({0,1,2,3})
