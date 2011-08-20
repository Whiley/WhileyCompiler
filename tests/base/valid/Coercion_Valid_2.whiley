import whiley.lang.*:*

[int] f(string x):
    return x

void System::main([string] args):
    this.out.println(str(f("Hello World")))