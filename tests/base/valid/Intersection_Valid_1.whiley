import * from whiley.lang.*

define EmptyList as [int] & [real]

int size(EmptyList l):
    return |l|

void ::main(System.Console sys):
    list = []
    sys.out.println(Any.toString(size(list)))
