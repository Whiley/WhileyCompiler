import * from whiley.lang.*

define pintset as {int}

void ::main(System.Console sys,[string] args):
    p = {1,2}
    sys.out.println(Any.toString(p))
