define scf2nat as int where $ >= 0

void f(scf2nat x):
    print str(x)
    x = -1
    print str(x)
    f(x) // recursive

void System::main([string] args):
    f(1)
    
