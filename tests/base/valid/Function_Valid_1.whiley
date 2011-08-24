import whiley.lang.*:*

string f(real x):
    return "GOT REAL"

string f(int x):
    return "GOT INT"

void ::main(System sys,[string] args):
    sys.out.println(f(1))
    sys.out.println(f(1.23))
