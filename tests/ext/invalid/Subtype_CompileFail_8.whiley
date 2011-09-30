import * from whiley.lang.*

define scf8nat as int where $ > 0
define scf8tup as {scf8nat f, int g} where g > f 

int f([scf8tup] xs):
    return |xs|

void ::main(System sys,[string] args):
    x = [{f:1,g:2},{f:4,g:8}]
    x[0].f = 2
    f(x)
