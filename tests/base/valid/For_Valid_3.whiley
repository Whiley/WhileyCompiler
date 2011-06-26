int sum({nat} xs):
    r = 0
    for x in xs:
        r = r + x
    return r

void System::main([string] args):
    z = sum({1,2,3,4,5})
    out.println(str(z))
    
