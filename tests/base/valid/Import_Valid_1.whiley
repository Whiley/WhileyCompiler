import whiley.lang.*

Type.nat f(int x):
    if x < 0:
        return 0
    else:
        return x

public void System::main([string] args):
    this.out.println(f(1))
