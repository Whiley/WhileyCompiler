import whiley.lang.*:*

void f(int x):
    this.out.println(str(x))

void System::main([string] args):
    this.f(1)
