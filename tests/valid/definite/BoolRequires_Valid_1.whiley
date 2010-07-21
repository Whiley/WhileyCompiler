void f([bool] x) where |x| > 0 && x[0]:
    print str(x)

void System::main([string] args):
    f([true])
    f([true,false])
    f([true,false,true])
    
