// this is a comment!
define int | real as IntReal

void f(int y):
    print str(y)

void System::main([string] args):
    IntReal x
    x = 123
    f(x)
    x = 1.234
    f(x)

