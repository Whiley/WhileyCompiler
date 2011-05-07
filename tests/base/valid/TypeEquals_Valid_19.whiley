define ilist as int | [int]
define rlist as real | [int]

string f(rlist e):
    if e ~= int:
        return "int"
    else if e ~= [int]:
        return "[int]"
    else:
        return "real"

string g(ilist e):
    return f(e)


void System::main([string] args):
    out<->println(f(1))
    out<->println(f([1]))
    out<->println(f([]))