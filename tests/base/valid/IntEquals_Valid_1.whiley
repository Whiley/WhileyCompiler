import whiley.lang.*:*

string f(int x, real y):
    if x == y:
        return "EQUAL"
    else:
        return "NOT EQUAL"

void ::main(System sys,[string] args):
    sys.out.println(f(1,4.0))
    sys.out.println(f(1,4.2))
    sys.out.println(f(0,0))
