define intreal as real | int

string f(intreal e):
    if e is int:
        return "int"
    else:
        return "real"

void System::main([string] args):
    this.out.println(f(1))
    this.out.println(f(1.134))
    this.out.println(f(1.0))
