import whiley.lang.*:*

int f(char x):
    return x

void System::main([string] args):
    this.out.println(str(f('H')))