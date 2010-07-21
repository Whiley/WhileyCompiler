void f({int} ls) where some { i in ls | i < 0}:
    print str(ls)

void g({int} ls):
    f(ls)

void System::main([string] args):
    g({1,2,3})
