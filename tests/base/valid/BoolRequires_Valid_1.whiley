string f([bool] x) requires |x| > 0 && x[0]:
    return str(x)

void System::main([string] args):
    out->println(f([true]))
    out->println(f([true,false]))
    out->println(f([true,false,true]))
    
