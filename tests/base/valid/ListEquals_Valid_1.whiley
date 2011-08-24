import whiley.lang.*:*

string f([int] xs, [real] ys):
    if xs == ys:
        return "EQUAL"
    else:
        return "NOT EQUAL"

void System::g([int] xs, [real] ys):
    this.out.println(str(xs))
    this.out.println(str(ys))
    this.out.println(f(xs,ys))

void ::main(System sys,[string] args):
    // following lines should block
    sys.g([1,4],[1.0,4.0])
    sys.g([1,4],[1.0,4.2])
    sys.g([],[])
