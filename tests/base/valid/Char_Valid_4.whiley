import * from whiley.lang.*

bool isChar(any x):
    if x is char:
        return true
    else:
        return false

void ::main(System.Console sys):
    sys.out.println(Any.toString(isChar('c')))
    sys.out.println(Any.toString(isChar(1)))
    sys.out.println(Any.toString(isChar([1,2,3])))
