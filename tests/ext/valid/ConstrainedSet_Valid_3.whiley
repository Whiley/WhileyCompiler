import * from whiley.lang.*

define posints as {int} where no { x in $ | x < 0 }

string f(posints x):
    return toString(x)

void ::main(System sys,[string] args):
    xs = {1,2,3}
    sys.out.println(f(xs))
