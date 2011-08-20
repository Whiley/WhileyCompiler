import whiley.lang.*:*

int f(int b):
    return b + 1

void System::main([string] args):
    b = f(10)
    this.out.println(str(b))
