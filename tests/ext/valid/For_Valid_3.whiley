int sum({nat} xs) ensures $ >= 0:
    r = 0
    for x in xs where r >= 0:
        r = r + x
    return r

void System::main([string] args):
    z = sum({1,2,3,4,5})
    out<->println(str(z))
    
