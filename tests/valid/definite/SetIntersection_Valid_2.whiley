void f({int} xs, {int} ys, {int} zs) requires zs == xs ∩ ys:
    print str(xs)

void g({int} ys):
    f(ys,ys,ys)

void h({int} ys, {int} zs):
    f(ys,zs,ys ∩ zs)

void System::main([string] args):
    g({})
    g({2})
    g({1,2,3})
    h({},{})
    h({1},{1,2})
    h({1,2,3},{3,4,5})
    h({1,2},{3,4,5})
