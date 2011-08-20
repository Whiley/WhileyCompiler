import whiley.lang.*:*

void f({int} xs) requires |xs| > 0:
    debug str(xs)

void g({int} ys, {int} zs):
    f(ys ∪ zs)

void System::main([string] args):
    g({},{1})
    g({2},{2})
    g({},{})
