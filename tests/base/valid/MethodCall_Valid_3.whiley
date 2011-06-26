void System::f(int x):
    out.println(str(x))

void System::main([string] args):
    // the following line should be an internal message send
    this.f(1)
    out.print("")
