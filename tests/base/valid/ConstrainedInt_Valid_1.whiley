import * from whiley.lang.*

// this is a comment!
define cr1nat as int

string f(cr1nat x):
    y = x
    return Any.toString(y)

void ::main(System sys,[string] args):
    sys.out.println(f(9))
