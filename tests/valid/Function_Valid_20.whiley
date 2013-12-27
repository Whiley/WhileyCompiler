import println from whiley.lang.System

define fr3nat as int where $ >= 0

string f(int x):
    return Any.toString(x)

void ::main(System.Console sys):
    y = 1
    sys.out.println(f(y))
