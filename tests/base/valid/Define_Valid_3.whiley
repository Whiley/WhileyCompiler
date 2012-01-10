import * from whiley.lang.*

// this is a comment!
define odd as { 1,3,5 }

void ::main(System.Console sys,[string] args):
    y = 1
    sys.out.println(Any.toString(y))
