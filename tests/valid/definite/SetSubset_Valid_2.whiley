void f({int} xs, {int} ys) requires xs ⊆ ys:
    out->println("XS IS A SUBSET")

void System::main([string] args):
    f({1,2,3},{1,2,3})
    f({1,2},{1,2,3})
    f({1},{1,2,3})
