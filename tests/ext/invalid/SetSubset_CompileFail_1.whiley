void f(int x, {int} ys) requires x ⊆ ys:
    debug "X IS A SUBSET"

void System::main([string] args):
    f({1,2},{1,2,3})
    f({1,4},{1,2,3})
