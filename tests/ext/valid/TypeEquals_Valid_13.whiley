import * from whiley.lang.*

define pos as int where $ > 0
define neg as int where $ < 0

define intlist as pos|neg|[int]

int f(intlist x):
    if x is int:
        return x
    return 1 

void ::main(System sys,[string] args):
    x = f([1,2,3])
    sys.out.println(str(x))
    x = f(123)
    sys.out.println(str(x))

