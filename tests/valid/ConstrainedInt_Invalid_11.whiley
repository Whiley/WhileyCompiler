import println from whiley.lang.System

// this is a comment!
define cr3nat as int

cr3nat f(cr3nat x):
    return 1

void ::main(System.Console sys):
    y = f(9)
    sys.out.println(Any.toString(y))
