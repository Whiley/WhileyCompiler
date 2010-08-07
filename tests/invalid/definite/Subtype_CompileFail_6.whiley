define scf6nat as int where $ > 0
define scf6tup as (scf6nat f, int g) requires g > f

void System::main([string] args):
    scf6tup x = (f:1,g:2)
    x.f = 2
    
