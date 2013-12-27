import println from whiley.lang.System

string f({int} xs) requires |xs| < 3:
    return Any.toString(xs)

string g({int} ys):
    return f(ys ∩ {1,2})

void ::main(System.Console sys):
    sys.out.println(g({}))
    sys.out.println(g({2,3,4,5,6}))
    sys.out.println(g({2,6}))
