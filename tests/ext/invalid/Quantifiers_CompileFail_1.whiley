void f({int} ls) requires some { i in ls | i < 0}:
    print str(ls)

void System::main([string] args):
    f({1,2,3})
