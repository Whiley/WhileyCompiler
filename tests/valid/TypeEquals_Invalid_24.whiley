import println from whiley.lang.System

define rlist as real | [int]

int f(rlist l):
    if l is real:
        return 0
    else:
        return |l|

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(123)))
    sys.out.println(Any.toString(f(1.23)))
    sys.out.println(Any.toString(f([1,2,3]))) 

