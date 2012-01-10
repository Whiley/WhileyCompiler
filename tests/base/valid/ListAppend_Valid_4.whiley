import * from whiley.lang.*

void ::main(System.Console sys,[string] args):
    l = [1,2,3]
    r = [4.23,5.5]
    r = r + l
    sys.out.println(Any.toString(r))
