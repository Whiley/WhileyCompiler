define sr8nat as int requires $ > 0
define sr8tup as (sr8nat f, int g) requires g > f 

void System::main([string] args):
    [sr8tup] x = [(f:1,g:3),(f:4,g:8)]
    x[0].f = 2
    print str(x)
    
