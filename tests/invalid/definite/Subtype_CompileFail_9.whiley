define int where $ > 0 as scf9nat
define (scf9nat f, int g) where g > f as scf9tup 
define [(scf9nat f, int g)] where some {z in $ | z.f == 1} as scf9arr

void System::main([string] args):
    scf9arr x = [(f:1,g:2),(f:4,g:8)]
    x[0].f = 2 // breaks scf9arr constraint
    
