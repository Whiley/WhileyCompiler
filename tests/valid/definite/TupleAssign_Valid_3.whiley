define (int f1, int f2) where f1 < f2 as tac3ta

void System::main([string] args):
    tac3ta x = (f1:2,f2:3)
    tac3ta y = (f1:1,f2:3)
    print str(x)
    print str(y)   
    assert x != y
    x.f1 = 1
    print str(x)
    print str(y)  
    assert x == y
