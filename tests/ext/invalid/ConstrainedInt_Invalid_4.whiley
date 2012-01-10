import * from whiley.lang.*

// this is a comment!
define c4nat as int where $ < 10

int h() ensures $ <= 5:
    return 5

c4nat f():
    return h() * 2

void ::main(System.Console sys):
    debug Any.toString(f())
