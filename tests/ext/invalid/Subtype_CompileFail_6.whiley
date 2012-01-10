import * from whiley.lang.*

define scf6nat as int where $ > 0
define scf6tup as {scf6nat f, int g} where g > f

int f(scf6tup x):
    return x.f

void ::main(System.Console sys,[string] args):
    x = {f:1,g:2}
    x.f = 2
    f(x)
    
