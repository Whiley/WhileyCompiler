import * from whiley.lang.*

bool test({real} xs, [int] ys):
    for x in (xs+ys):
        if x == 3:
            return true
    return false

void ::main(System sys,[string] args):
    s = test({1.2,2.3,3.4},[1,2,3,4,5,6,7,8])
    sys.out.println(str(s))
    s = test({1.2,2.3,3.4},[])
    sys.out.println(str(s))
