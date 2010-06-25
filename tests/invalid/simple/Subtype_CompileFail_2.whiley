define int where $ >= 0 as scf2nat

void f(scf2nat x):
    print str(x)
    x = -1
    print str(x)

void System::main([string] args):
    f(1)
    
