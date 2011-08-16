define src as int|[int]|[[int]]

string f(src e):
    if e is [any]:
        return "[*]"
    else:
        return "int"

void System::main([string] args):
    this.out.println(f([1,2,3]))
    this.out.println(f([[1],[2]]))
    this.out.println(f(1))
