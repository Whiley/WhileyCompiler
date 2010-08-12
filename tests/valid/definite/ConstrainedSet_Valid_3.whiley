define posints as {int} where no { x in $ | x < 0 }

void f(posints x):
    print str(x)

void System::main([string] args):
    posints xs = {1,2,3}
    f(xs)
