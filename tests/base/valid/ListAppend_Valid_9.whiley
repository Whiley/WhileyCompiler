bool test([real] xs, [int] ys):
    for x in (xs+ys):
        if x is int:
            return true
    return false

void System::main([string] args):
    s = test([1.2,2.3,3.4],[1,2,3,4,5,6,7,8])
    out.println(str(s))
    s = test([1.2,2.3,3.4],[])
    out.println(str(s))
