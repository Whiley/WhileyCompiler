import * from whiley.lang.*

define fr3nat as int where $ >= 0

string f(int x):
    return Any.toString(x)

void ::main(System.Console sys,[string] args):
    y = 1
    sys.out.println(f(y))
