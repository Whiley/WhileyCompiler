import * from whiley.lang.*

void ::main(System.Console sys):
    r = [1,2] + [3,4]
    sys.out.println(Any.toString(r))
