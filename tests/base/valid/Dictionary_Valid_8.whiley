import * from whiley.lang.*

define DL1 as {int=>int}
define DL2 as {int=>real}

DL2 update(DL1 ls):
    ls[0] = 1.234
    return ls

void ::main(System sys,[string] args):
    x = {0=>1, 1=>2}
    x = update(x)
    sys.out.println(Any.toString(x))
