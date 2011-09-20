import * from whiley.lang.*

void ::main(System sys,[string] args):
    left = [1,2]
    right = [3,4]
    r = left + right
    left = left + [6]
    sys.out.println(str(left))
    sys.out.println(str(right))
    sys.out.println(str(r))
