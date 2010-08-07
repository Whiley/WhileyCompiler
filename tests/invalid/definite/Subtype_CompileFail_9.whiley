define scf9nat as int requires $ > 0
define scf9tup as (scf9nat f, int g) requires g > f 
define scf9arr as [(scf9nat f, int g)] requires some {z in $ | z.f == 1}

void System::main([string] args):
    scf9arr x = [(f:1,g:2),(f:4,g:8)]
    x[0].f = 2 // breaks scf9arr constraint
    
