import whiley.lang.*:*

void System::main([string] args):
    st = "Hello World"
    for st in args:
        this.out.println(st)
