// This example was inspired by comments from Stuart Marshall.

define int where $ >= 0 as anat
define int where 2*$ >= $ as bnat

bnat atob(anat x):
    return x

anat btoa(bnat x):
    return x

void System::main([string] args):
    int x = 1
    print str(atob(x))
    print str(btoa(x))
    
    
