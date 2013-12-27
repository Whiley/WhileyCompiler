import println from whiley.lang.System

string f({int} xs) requires xs ⊆ {1,2,3}:
    return Any.toString(xs)

string g({int} ys):
    return f(ys ∩ {1,2,3})

void ::main(System.Console sys):
    sys.out.println(g({1,2,3,4}))
    sys.out.println(g({2}))
    sys.out.println(g({}))
