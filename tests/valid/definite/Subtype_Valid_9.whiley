define int where $ > 0 as sr9nat
define (sr9nat f, int g) where g > f as sr9tup 
define [(sr9nat f, int g)] where some { z in $ | z.f == 1} as sr9arr

void System::main([string] args):
    sr9arr x = [(f:1,g:2),(f:1,g:8)]
    x[0].f = 2 
    print str(x)
