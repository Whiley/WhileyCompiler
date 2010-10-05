void f({int} xs) requires |xs| > 0:
    out->println(str(xs))

void g({int} ys):
    f(ys ∪ {1})

void System::main([string] args):
    g({})
    g({2})
