{(int,real)} f({int->real} x):
    return x

void System::main([string] args):
    x = f({1->2.2,2->3.3})
    this.out.println(str(x))