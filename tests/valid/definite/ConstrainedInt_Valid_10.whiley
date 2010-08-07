// This example was inspired by comments from Stuart Marshall.

anat as int where $ >= 0
bnat as int where 2*$ >= $

bnat atob(anat x):
    return x

anat btoa(bnat x):
    return x

void System::main([string] args):
    int x = 1
    print str(atob(x))
    print str(btoa(x))
    
    
