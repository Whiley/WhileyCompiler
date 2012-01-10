import * from whiley.lang.*

define intList as int|[int]
define tup as {int mode, intList data}

[{int mode, int data}] f([{int mode, int data}] x):
    return x

void ::main(System.Console sys):
    tups = [{mode:0,data:1},{mode:1,data:[1,2,3]}]
    tups[0].data = 1
    tups = f(tups) // NOT OK
    sys.out.println(Any.toString(tups))

