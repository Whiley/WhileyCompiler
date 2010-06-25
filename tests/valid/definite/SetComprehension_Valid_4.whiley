{int} f([int] xs):
    return { x | x in xs, x > 1 }

void System::main([string] args):
    print str(f([1,2,3]))
    print str(f([1,2,3,3]))
    print str(f([-1,1,2,-1,3,3]))
