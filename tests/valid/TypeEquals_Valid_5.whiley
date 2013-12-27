import println from whiley.lang.System

string f(int|null x):
    if x is null:
        return "GOT NULL"
    else:
        return "GOT INT"

void ::main(System.Console sys):
    x = null
    sys.out.println(f(x))
    sys.out.println(f(1))
