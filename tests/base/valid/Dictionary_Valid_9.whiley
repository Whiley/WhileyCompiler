import println from whiley.lang.System

define DL1 as {int=>int}
define DL2 as {real=>int}

DL2 update(DL1 ls):
    ls[1.2] = 1
    return ls

void ::main(System.Console sys):
    x = {0=>1, 1=>2}
    x = update(x)
    sys.out.println(Any.toString(x))
