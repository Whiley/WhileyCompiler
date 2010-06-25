define (int f1, int f2) where f1 < f2 as tac2ta
define (int f1, int f2) where (f1+1) < f2 as tac2tb

void System::main([string] args):
    tac2ta x = (f1:2,f2:3)
    tac2tb y
    print str(x)
    x.f1 = 1
    y = x
    print str(y)
