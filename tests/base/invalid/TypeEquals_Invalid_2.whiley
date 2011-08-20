import whiley.lang.*:*

define ilist as real | [int]

string f(real e):
    if e is real:
        return "real"
    else if e is int:
        return "int"
    else:
        return "[int]"

void System::main([string] args):
    this.out.println(f(1))
