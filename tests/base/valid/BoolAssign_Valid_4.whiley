int f(int x, int y):
    a = true
    
    if(x < y):
        a = false
    
    if(!a):
        return x + y
    else:
        return 123

void System::main([string] args):
    out->println(str(1))
