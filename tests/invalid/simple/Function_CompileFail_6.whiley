define anat as int where $ >= 0
define bnat as int where 2*$ >= $

int f(anat x):
    return x

int f(bnat x):
    return x

void System::main([string] args):    
    print str(f(1))
    
    
