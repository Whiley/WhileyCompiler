import whiley.lang.*:*

string g(int z):
    return str(z)

string f(int x):
    y = x + 1
    return g(y)

void System::main([string] args):
    this.out.println(f(1))
