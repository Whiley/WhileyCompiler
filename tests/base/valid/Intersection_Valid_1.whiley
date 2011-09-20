import * from whiley.lang.*

define EmptyList as [int] & [real]

int size(EmptyList l):
    return |l|

void ::main(System sys, [string] args):
    list = []
    sys.out.println(str(size(list)))
