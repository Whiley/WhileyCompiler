real f(real x):
    return -x

void System::main([string] args):
    this.out.println(str(f(1.2)))
    this.out.println(str(f(0.00001)))
    this.out.println(str(f(5632)))
