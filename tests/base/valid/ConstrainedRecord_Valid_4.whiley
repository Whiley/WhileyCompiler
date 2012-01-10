import * from whiley.lang.*

define point as {int x, int y}

void ::main(System.Console sys,[string] args):
    p = {x:1,y:1}
    sys.out.println(Any.toString(p))
