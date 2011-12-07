import * from whiley.lang.*

define fr3nat as int

string f(int x):
    return Any.toString(x)

void ::main(System sys,[string] args):
    y = 234987234987234982304980130982398723
    sys.out.println(f(y))
