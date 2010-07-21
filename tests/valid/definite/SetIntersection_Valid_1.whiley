void f({int} xs) where xs ⊆ {1,2,3}:
    print str(xs)

void g({int} ys):
    f(ys ∩ {1,2,3})

void System::main([string] args):
    g({1,2,3,4})
    g({2})
    g({})
