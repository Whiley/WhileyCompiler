// This example was inspired by comments from Stuart Marshall.

define anat as int requires $ >= 0
define bnat as int requires 2*$ >= $

bnat atob(anat x):
    return x

anat btoa(bnat x):
    return x

void System::main([string] args):
    int x = 1
    print str(atob(x))
    print str(btoa(x))
    
    
