import * from whiley.lang.*

// this is a comment!
define nat as int

nat f():
    return 1

void ::main(System.Console sys,[string] args):
    sys.out.println(Any.toString(f()))
