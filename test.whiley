void f([int] xs):
    xs[0] = 1

void System::main([string] args):
    [int] ys = [2]
    f(ys)
    print str(ys)
