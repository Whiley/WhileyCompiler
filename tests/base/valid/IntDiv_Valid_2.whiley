import whiley.lang.*:*

int f(int x):
    return x / 3

public void System::main([string] args):
    this.out.println(str(f(10)))
