import whiley.lang.*:*

define dummy as process {int x}

void dummy::f(int x):
    this.out.println(str(x))

void System::main([string] args):
    this.f(1)
