import whiley.lang.*:*

string f(int|null x):
    if x is null:
        return "GOT NULL"
    else:
        return "GOT INT"

void System::main([string] args):
    x = null
    this.out.println(f(x))
    this.out.println(f(1))
