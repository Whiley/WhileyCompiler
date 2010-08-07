void f({int} xs, {int} ys) requires xs ⊂ ys:
    print "XS IS A SUBSET"

void g({int} xs, {int} ys) requires xs ⊂ ys:
    f(xs,ys)

void System::main([string] args):
    g({1,2},{1,2,3})
    g({1},{1,2,3})