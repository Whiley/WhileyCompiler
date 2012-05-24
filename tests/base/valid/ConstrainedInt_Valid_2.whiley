import println from whiley.lang.System

// this is a comment!
define cr2num as {1,2,3,4}

string f(cr2num x):
    y = x
    return Any.toString(y)

void ::main(System.Console sys):
    sys.out.println(f(3))
