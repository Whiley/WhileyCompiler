import println from whiley.lang.System

string f({int} xs):
    if |xs| > 0:
        return Any.toString(xs)
    else:
        return "FAILED"

string g({int} ys):
    return f(ys ∪ {1})

void ::main(System.Console sys):
    sys.out.println(g({}))
    sys.out.println(g({2}))
