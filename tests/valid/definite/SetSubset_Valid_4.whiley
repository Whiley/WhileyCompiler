void f({int} xs, {int} ys) where xs ⊂ ys:
    print "XS IS A SUBSET"

void System::main([string] args):
    f({1,2},{1,2,3})
    f({1},{1,2,3})
