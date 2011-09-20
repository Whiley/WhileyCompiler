import * from whiley.lang.*

define DL1 as {int->int}
define DL2 as {any->int}

DL2 update(DL1 ls):
    ls[1.2] = 1
    return ls

void ::main(System sys,[string] args):
    x = {0->1, 1->2}
    x = update(x)
    sys.out.println(str(x))
