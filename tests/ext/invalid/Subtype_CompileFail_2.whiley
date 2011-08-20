import whiley.lang.*:*

define scf2nat as int where $ >= 0

void f(scf2nat x):
    debug str(x)
    x = -1
    debug str(x)
    f(x) // recursive

void System::main([string] args):
    f(1)
    
