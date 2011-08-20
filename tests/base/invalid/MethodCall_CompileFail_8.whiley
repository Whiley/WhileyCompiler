import whiley.lang.*:*

int f():
    x = spawn 1
    return 1

void System::main([string] args):
    x = f()
    this.out.println(str(x))
