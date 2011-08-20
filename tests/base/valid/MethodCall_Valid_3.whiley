import whiley.lang.*:*

void System::f(int x):
    this.out.println(str(x))

void System::main([string] args):
    // the following line should be an internal message send
    this.f(1)
    this.out.print("")
