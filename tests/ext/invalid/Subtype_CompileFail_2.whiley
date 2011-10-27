import * from whiley.lang.*

define scf2nat as int where $ >= 0

void f(scf2nat x):
    debug toString(x)
    x = -1
    debug toString(x)
    f(x) // recursive

void ::main(System sys,[string] args):
    f(1)
    
