define int where $ > 0 as scf8nat
define (scf8nat f, int g) where g > f as scf8tup 

void System::main([string] args):
    [scf8tup] x = [(f:1,g:2),(f:4,g:8)]
    x[0].f = 2
    
