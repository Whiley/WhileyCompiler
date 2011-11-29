import * from whiley.lang.*

define fr6nat as int where $ >= 0

{fr6nat} g({fr6nat} xs):
    return { y | y in xs, y > 1 }

string f({int} x):
    return toString(x)

void ::main(System sys,[string] args):
    ys = {1,2,3}
    sys.out.println(f(g(ys)))
