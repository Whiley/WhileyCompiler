// the following line should simply not compile.
define junk as junk | int

int f(junk x):
    return x

void System::main([string] args):
    x = 1
    this.out.println(str(f(x)))