import whiley.lang.*:*

string f([int] xs, [real] ys):
    if xs == ys:
        return "EQUAL"
    else:
        return "NOT EQUAL"

void System::g([int] xs, [real] ys):
    sys.out.println(str(xs))
    sys.out.println(str(ys))
    sys.out.println(f(xs,ys))

void ::main(System sys,[string] args):
    // following lines should block
    this.g([1,4],[1.0,4.0])
    this.g([1,4],[1.0,4.2])
    this.g([],[])
