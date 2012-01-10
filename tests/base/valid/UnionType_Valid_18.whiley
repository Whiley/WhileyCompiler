import * from whiley.lang.*

define TYPE as null|int

int f([TYPE] xs, TYPE p):
    r = 0
    for x in xs:
        if x == p:
            return r
        r = r + 1
    return -1

void ::main(System.Console sys,[string] args):
    sys.out.println(Any.toString(f([null,1,2],null)))
    sys.out.println(Any.toString(f([1,2,null,10],10)))
