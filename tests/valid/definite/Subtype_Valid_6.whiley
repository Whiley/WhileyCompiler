define int where $ > 0 as sr6nat
define (sr6nat f, int g) where g > f as sr6tup

void System::main([string] args):
    sr6tup x = (f:1,g:5)
    x.f = 2
    print str(x)
    
