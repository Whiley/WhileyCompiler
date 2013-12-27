import println from whiley.lang.System

// this is a comment!
define ir1nat as int
define pir1nat as ir1nat

string f(int x):
    if x > 2:
        y = x
        return Any.toString(y)
    return ""

void ::main(System.Console sys):
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(3))
