import * from whiley.lang.*

define scf2nat as int where $ >= 0

void f(scf2nat x):
    debug Any.toString(x)
    x = -1
    debug Any.toString(x)
    f(x) // recursive

void ::main(System.Console sys,[string] args):
    f(1)
    
