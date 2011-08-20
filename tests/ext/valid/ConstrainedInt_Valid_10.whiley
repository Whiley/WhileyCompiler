import whiley.lang.*:*

// This example was inspired by comments from Stuart Marshall.

define anat as int where $ >= 0
define bnat as int where 2*$ >= $

bnat atob(anat x):
    return x

anat btoa(bnat x):
    return x

void System::main([string] args):
    x = 1
    this.out.println(str(atob(x)))
    this.out.println(str(btoa(x)))
    
    
