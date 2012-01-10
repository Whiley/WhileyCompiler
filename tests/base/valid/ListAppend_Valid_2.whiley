import * from whiley.lang.*

void ::main(System.Console sys,[string] args):
    left = [1,2]
    right = [3,4]
    r = left + right
    sys.out.println(Any.toString(r))
