void f({int} xs, {int} ys) where xs ⊂ ys:
    print "XS IS A SUBSET"

void g({int} xs, {int} ys) where xs ⊆ ys:
    f(xs,ys)

void System::main([string] args):
    g({1,2,3},{1,2,3})
    g({1},{1,2,3})