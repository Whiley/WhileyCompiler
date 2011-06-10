define Expr as real | [Expr]
define Value as real | [Value]

Value init():
    return 0.0123

void System::main([string] args):
    v = init()
    if v ~= [Expr]:
        out.println("GOT LIST")
    else:
        out.println(str(v))