import * from whiley.lang.*

void ::main(System sys,[string] args):
    xs = [1,2,3]
    for st in xs:
        sys.out.println(Any.toString(st))
