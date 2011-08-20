import whiley.lang.*:*

string f(bool b):
    return str(b)

void System::main([string] args):
    x = true
    this.out.println(f(x))
    x = false
    this.out.println(f(x))
