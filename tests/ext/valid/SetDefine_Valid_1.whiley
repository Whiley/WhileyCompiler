import * from whiley.lang.*

define pintset as {int} where |$| > 1

void ::main(System sys,[string] args):
    p = {1,2}
    sys.out.println(toString(p))
