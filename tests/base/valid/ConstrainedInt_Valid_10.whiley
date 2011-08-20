import whiley.lang.*:*

// This example was inspired by comments from Stuart Marshall.

define anat as int
define bnat as int

bnat atob(anat x):
    return x

anat btoa(bnat x):
    return x

void System::main([string] args):
    x = 1
    this.out.println(str(atob(x)))
    this.out.println(str(btoa(x)))
    
    
