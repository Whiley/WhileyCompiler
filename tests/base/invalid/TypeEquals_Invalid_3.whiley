define ilist as real | [int]

string f(ilist e):
    if e ~= int:
        return "int"
    else:
        return "real | [int]"

void System::main([string] args):
    out.println(f(1))
