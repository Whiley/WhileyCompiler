import * from whiley.lang.*

define rlist as real | [int]

int f(rlist l):
    if l is real:
        return 0
    else:
        return |l|

void ::main(System sys,[string] args):
    sys.out.println(toString(f(123)))
    sys.out.println(toString(f(1.23)))
    sys.out.println(toString(f([1,2,3]))) 

