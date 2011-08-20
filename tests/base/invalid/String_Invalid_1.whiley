import whiley.lang.*:*

void System::main([string] args):
    s = "Hello World"
    s[0] = 1.234
    this.out.println(s)
