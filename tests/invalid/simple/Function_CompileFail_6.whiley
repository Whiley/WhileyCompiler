define anat as int requires $ >= 0
define bnat as int requires 2*$ >= $

int f(anat x):
    return x

int f(bnat x):
    return x

void System::main([string] args):    
    print str(f(1))
    
    
