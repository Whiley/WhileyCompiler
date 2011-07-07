define dummy as process {int x}

void dummy::f(int x):
    out.println(str(x))

void System::main([string] args):
    this.f(1)
