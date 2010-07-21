void f({int} xs) where |xs| < 3:
    print str(xs)

void g({int} ys):
    f(ys ∩ {1,2})

void System::main([string] args):
    g({})
    g({2,3,4,5,6})
    g({2,6})
