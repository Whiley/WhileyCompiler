define scf7nat as int where $ < 0
define scf7tup as {scf7nat f}

int f(scf7tup x):
    return x.f

void System::main([string] args):
    x = {f:-1}
    x.f = x.f + 1
    f(x)
    
