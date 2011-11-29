import * from whiley.lang.*

string f(bool b):
    return toString(b)

void ::main(System sys,[string] args):
    x = true
    sys.out.println(f(x))
    x = false
    sys.out.println(f(x))
