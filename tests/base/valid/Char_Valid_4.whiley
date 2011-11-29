import * from whiley.lang.*

bool isChar(any x):
    if x is char:
        return true
    else:
        return false

void ::main(System sys,[string] args):
    sys.out.println(toString(isChar('c')))
    sys.out.println(toString(isChar(1)))
    sys.out.println(toString(isChar([1,2,3])))
