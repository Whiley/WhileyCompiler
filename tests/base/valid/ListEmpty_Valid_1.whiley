import * from whiley.lang.*

string f([int] xs):
    return Any.toString(xs)

void ::main(System sys,[string] args):
    sys.out.println(f([1,4]))
    sys.out.println(f([]))
