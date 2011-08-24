import whiley.lang.*:*

define pintset as {int} where |$| > 1

int f(pintset x):
    return |x|

void ::main(System sys,[string] args):
    p = {1}
    debug str(p)
    f(p)
