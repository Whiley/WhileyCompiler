import whiley.lang.*:*

void f({int} ls) requires no { i in ls | i <= 0}:
    debug str(ls)

void g({int} ls):
    f(ls)

void System::main([string] args):
    g({0,1,2,3})
