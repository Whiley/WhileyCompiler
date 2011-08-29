import whiley.lang.*:*

define EmptyList as [int] & [real]

int size(EmptyList l):
    return |l|

void ::main(System sys, [string] args):
    list = [1]
    sys.out.println(str(size(list)))
