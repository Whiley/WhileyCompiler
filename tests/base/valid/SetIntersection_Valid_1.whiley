string f({int} xs) requires xs ⊆ {1,2,3}:
    return str(xs)

string g({int} ys):
    return f(ys ∩ {1,2,3})

void System::main([string] args):
    out->println(g({1,2,3,4}))
    out->println(g({2}))
    out->println(g({}))
