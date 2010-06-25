define int where $ > 0 as scf6nat
define (scf6nat f, int g) where g > f as scf6tup

void System::main([string] args):
    scf6tup x = (f:1,g:2)
    x.f = 2
    
