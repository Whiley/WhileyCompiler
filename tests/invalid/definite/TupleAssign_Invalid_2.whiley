define tac2ta as (int f1, int f2) where f1 < f2
define tac2tb as (int f1, int f2) where (f1+1) < f2

void System::main([string] args):
    tac2ta x = (f1:1,f2:3)
    tac2tb y
    print str(x)
    x.f1 = 2
    y = x
    print str(y)
