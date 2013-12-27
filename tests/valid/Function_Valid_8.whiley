import println from whiley.lang.System

define fr6nat as int where $ >= 0

{fr6nat} g({fr6nat} xs):
    return { y | y in xs, y > 1 }

string f({int} x):
    return Any.toString(x)

void ::main(System.Console sys):
    ys = {1,2,3}
    sys.out.println(f(g(ys)))
