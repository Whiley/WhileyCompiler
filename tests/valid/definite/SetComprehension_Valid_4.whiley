{int} f([int] xs):
    return { x | x in xs, x > 1 }

void System::main([string] args):
    out->println(str(f([1,2,3])))
    out->println(str(f([1,2,3,3])))
    out->println(str(f([-1,1,2,-1,3,3])))
