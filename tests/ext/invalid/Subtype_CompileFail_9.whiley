import whiley.lang.*:*

define scf9nat as int where $ > 0
define scf9tup as {scf9nat f, int g} where g > f 
define scf9arr as [{scf9nat f, int g}] where some {z in $ | z.f == 1}

int f(scf9arr xs):
    return |xs|

void System::main([string] args):
    x = [{f:1,g:2},{f:4,g:8}]
    x[0].f = 2 // breaks scf9arr constraint
    f(x)
