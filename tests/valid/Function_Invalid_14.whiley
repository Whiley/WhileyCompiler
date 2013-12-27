import println from whiley.lang.System

define fr5nat as int where $ >= 0

{fr5nat} g({fr5nat} xs):
    return { y | y in xs, y > 1 }

string f({fr5nat} x):
    return Any.toString(x)

void ::main(System.Console sys):
    ys = {1,2,3}
    sys.out.println(f(g(ys)))
