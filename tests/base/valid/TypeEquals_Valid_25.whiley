define src as int|[src]

string f(src e):
    if e is [any]:
        return "[*]"
    else:
        return "int"

void System::main([string] args):
    out.println(f([1]))
    out.println(f([[1]]))
    out.println(f([[[1]]]))
    out.println(f(1))
