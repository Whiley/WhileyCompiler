define scf2nat as int requires $ >= 0

void f(scf2nat x):
    print str(x)
    x = -1
    print str(x)

void System::main([string] args):
    f(1)
    
