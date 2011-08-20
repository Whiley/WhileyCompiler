import whiley.lang.*

int f(Type.nat x):
    return x-1

public void System::main([string] args):
    this.out.println(f(1))
