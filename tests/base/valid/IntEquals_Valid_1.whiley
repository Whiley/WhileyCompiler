import whiley.lang.*:*

string f(int x, real y):
    if x == y:
        return "EQUAL"
    else:
        return "NOT EQUAL"

void System::main([string] args):
    this.out.println(f(1,4.0))
    this.out.println(f(1,4.2))
    this.out.println(f(0,0))
