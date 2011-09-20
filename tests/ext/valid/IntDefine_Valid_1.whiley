import * from whiley.lang.*

// this is a comment!
define ir1nat as int where $ > 0
define pir1nat as ir1nat where $ > 1

string f(int x):
    if x > 2:
        y = x
        return str(y)
    return ""

void ::main(System sys,[string] args):
    sys.out.println(f(1))
    sys.out.println(f(2))
    sys.out.println(f(3))
