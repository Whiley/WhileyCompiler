define int where $ >= 0 as anat
define int where 2*$ >= $ as bnat

int f(anat x):
    return x

int f(bnat x):
    return x

void System::main([string] args):    
    print str(f(1))
    
    
