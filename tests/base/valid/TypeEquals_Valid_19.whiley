define ilist as int | [int]
define rlist as real | [int]

string f(rlist e):
    if e is [int]:
        return "[int]"
    else:
        return "real"

string g(ilist e):
    return f(e)


void System::main([string] args):
    this.out.println(f(1))
    this.out.println(f([1]))
    this.out.println(f([]))
