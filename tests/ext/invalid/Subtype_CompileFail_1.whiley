import whiley.lang.*:*

define scf1nat as int where $ >= 0

int f(sc1nat x):
    return x

void System::main([string] args):
    x = -1
    f(x)
