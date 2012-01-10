import * from whiley.lang.*

string f(bool b):
    return Any.toString(b)

void ::main(System.Console sys,[string] args):
    x = true
    sys.out.println(f(x))
    x = false
    sys.out.println(f(x))
