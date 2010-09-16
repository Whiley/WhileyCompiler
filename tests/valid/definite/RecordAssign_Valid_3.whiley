void System::main([string] args):
    x = {f1:2,f2:3}
    y = {f1:1,f2:3}
    print str(x)
    print str(y)   
    assert x != y
    x.f1 = 1
    print str(x)
    print str(y)  
    assert x == y
