define src as int|[int]|[[int]]

string f(src e):
    if e is [*]:
        return "[*]"
    else:
        return "int"

void System::main([string] args):
    out.println(f([1,2,3]))
    out.println(f([[1],[2]]))
    out.println(f(1))
