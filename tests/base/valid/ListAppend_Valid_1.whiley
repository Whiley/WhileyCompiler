import * from whiley.lang.*

void ::main(System.Console sys,[string] args):
    r = [1,2] + [3,4]
    sys.out.println(Any.toString(r))
