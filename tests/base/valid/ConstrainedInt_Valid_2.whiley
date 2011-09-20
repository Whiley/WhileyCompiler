import * from whiley.lang.*

// this is a comment!
define cr2num as {1,2,3,4}

string f(cr2num x):
    y = x
    return str(y)

void ::main(System sys,[string] args):
    sys.out.println(f(3))
