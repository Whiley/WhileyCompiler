string f([int] xs, [real] ys):
    if xs == ys:
        return "EQUAL"
    else:
        return "NOT EQUAL"

void System::g([int] xs, [real] ys):
    out->println(str(xs))
    out->println(str(ys))
    out->println(f(xs,ys))

void System::main([string] args):
    this->g([1,4],[1.0,4.0])
    this->g([1,4],[1.0,4.2])
    this->g([],[])
