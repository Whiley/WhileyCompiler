void f([int] xs, [real] ys):
    out->println(str(xs))
    out->println(str(ys))
    if xs == ys:
        out->println("EQUAL")
    else:
        out->println("NOT EQUAL")

void System::main([string] args):
    f([1,4],[1.0,4.0])
    f([1,4],[1.0,4.2])
    f([],[])
