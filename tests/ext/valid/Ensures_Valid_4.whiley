int f(int x) requires x>=0, ensures $>=0 && x>=0:
    return x

void System::main([string] args):
    this.out.println(str(f(10)))
