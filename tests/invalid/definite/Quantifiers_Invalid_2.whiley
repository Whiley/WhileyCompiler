void f({int} ls) requires no { i in ls | i <= 0}:
    print str(ls)

void g({int} ls):
    f(ls)

void System::main([string] args):
    g({0,1,2,3})
