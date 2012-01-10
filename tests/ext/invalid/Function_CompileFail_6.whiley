import * from whiley.lang.*

define anat as int where $ >= 0
define bnat as int where 2*$ >= $

int f(anat x):
    return x

int f(bnat x):
    return x

void ::main(System.Console sys,[string] args):    
    debug Any.toString(f(1))
    
    
