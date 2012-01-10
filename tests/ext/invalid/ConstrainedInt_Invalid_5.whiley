import * from whiley.lang.*

// this is a comment!
define nat as {1,2,3,4,5}

int h() ensures $ <= 3:
    return 0

nat f():
    return h()

void ::main(System.Console sys,[string] args):
    debug Any.toString(f())
