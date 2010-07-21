void f({int} xs) where |xs| > 0:
    print str(xs)

void g({int} ys, {int} zs):
    f(ys ∪ zs)

void System::main([string] args):
    g({},{1})
    g({2},{2})
    g({},{})
