

void f({int} xs) requires xs != ∅:
    debug Any.toString(xs)

void ::main(System.Console sys):
    f({1,4})
    f({})
