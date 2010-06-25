define int where $ > 0 as sr8nat
define (sr8nat f, int g) where g > f as sr8tup 

void System::main([string] args):
    [sr8tup] x = [(f:1,g:3),(f:4,g:8)]
    x[0].f = 2
    print str(x)
    
