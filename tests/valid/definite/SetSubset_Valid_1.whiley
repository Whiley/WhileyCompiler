void f({int} xs, {int} ys) where |xs| <= |ys|:
    if xs ⊆ ys:
        print "XS IS A SUBSET"
    else:
        print "XS IS NOT A SUBSET"

void System::main([string] args):
    f({1,2,3},{1,2,3})
    f({1,4},{1,2,3})
    f({1},{1,2,3})
