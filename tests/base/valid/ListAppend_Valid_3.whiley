import * from whiley.lang.*

void ::main(System sys,[string] args):
    left = [1,2]
    right = [3,4]
    r = left + right
    left = left + [6]
    sys.out.println(toString(left))
    sys.out.println(toString(right))
    sys.out.println(toString(r))
