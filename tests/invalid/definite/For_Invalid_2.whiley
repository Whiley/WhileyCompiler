int sum({int} xs) ensures $ >= 0:
    int r = 0
    for x in xs where r >= 0:
        r = r + x
    return r

void System::main([string] args):
    int z = sum({1,2,3,4,5})
    print str(z)
    
