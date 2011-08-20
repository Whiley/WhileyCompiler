import whiley.lang.*:*

int f(int x):
    return x

void System::main([string] args):
    x = 1
    x = f(x + 2.3)
