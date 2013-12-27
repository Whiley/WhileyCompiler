import println from whiley.lang.System

define fr6nat as int

{fr6nat} g({fr6nat} xs):
    return { y | y in xs, y > 1 }

string f({int} x):
    return Any.toString(x)

void ::main(System.Console sys):
    ys = {-12309812,1,2,2987,2349872,234987234987,234987234987234}
    sys.out.println(f(g(ys)))
