// this is a comment!
define {1,2,3,4} as c3num

void f(c3num x):
    c3num y
    y = x
    print str(y)

void g(int z):
    f(z)

void System::main([string] args):
    g(5)
