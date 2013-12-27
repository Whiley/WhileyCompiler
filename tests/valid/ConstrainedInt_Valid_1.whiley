import println from whiley.lang.System

// this is a comment!
define cr1nat as int

string f(cr1nat x):
    y = x
    return Any.toString(y)

void ::main(System.Console sys):
    sys.out.println(f(9))
