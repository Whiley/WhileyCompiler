

void f({int} xs) requires xs ⊆ {1,2,3}:
    debug Any.toString(xs)

void g({int} ys):
    f(ys ∩ {1,2,3,4})

void ::main(System.Console sys):
    g({1,2,3,4})
    g({2})
    g({})
