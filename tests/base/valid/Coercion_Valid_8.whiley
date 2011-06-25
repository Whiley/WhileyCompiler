define Expr as real | [Expr]

real f(Expr x):
    if x is [Expr]:
        return |x|
    else:
        return x

void System::main([string] args):
    out.println(str(f([1,2,3])))
    out.println(str(f([1.0,2.0,3.0])))
    out.println(str(f(1)))
    out.println(str(f(1.234)))
