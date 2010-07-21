[int] f() where |$| > 1:
    return [1,2]

void System::main([string] args):
     [int] a1
     [int] a2
     
     a1 = f()
     a2 = f()
     a2[0] = 0
     
     print str(a1[0])
     print str(a1[1])
     print str(a2[0])
     print str(a2[1])
