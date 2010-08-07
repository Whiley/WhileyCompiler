int f(int x, int y) requires x>=0 && y>=0, ensures $>0:
    bool a
    a = true
    
    if(x < y):
        a = false
    
    if(!a):
        return x + y
    else:
        return 123

void System::main([string] args):
    print str(1)
