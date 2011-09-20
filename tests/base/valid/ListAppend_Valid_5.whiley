import * from whiley.lang.*

int f([int] xs):
    return |xs|

void ::main(System sys,[string] args):
    left = [1,2,3]
    right = [5,6,7]
    r = f(left + right)
    sys.out.println(str(r))
