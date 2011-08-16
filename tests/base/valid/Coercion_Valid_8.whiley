define Expr as real | [Expr]

real f(Expr x):
    if x is [Expr]:
        return |x|
    else:
        return x

void System::main([string] args):
    this.out.println(str(f([1,2,3])))
    this.out.println(str(f([1.0,2.0,3.0])))
    this.out.println(str(f(1)))
    this.out.println(str(f(1.234)))
