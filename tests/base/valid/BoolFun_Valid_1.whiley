import println from whiley.lang.System

string f(bool b):
    return Any.toString(b)

void ::main(System.Console sys):
    x = true
    sys.out.println(f(x))
    x = false
    sys.out.println(f(x))
