import println from whiley.lang.System

// this is a comment!
define nat as int where $ < 10

nat f():
    return 1

void ::main(System.Console sys):
    sys.out.println(Any.toString(f()))
