

void f({int} ls) requires some { i in ls | i < 0}:
    debug Any.toString(ls)

void ::main(System.Console sys):
    f({1,2,3})
