import println from whiley.lang.System

// This example was inspired by comments from Stuart Marshall.

define anat as int
define bnat as int

bnat atob(anat x):
    return x

anat btoa(bnat x):
    return x

void ::main(System.Console sys):
    x = 1
    sys.out.println(Any.toString(atob(x)))
    sys.out.println(Any.toString(btoa(x)))
    
    
