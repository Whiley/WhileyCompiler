string f({int} xs) requires |xs| > 0:
    return str(xs)

string g({int} ys):
    return f(ys ∪ {1})

void System::main([string] args):
    out<->println(g({}))
    out<->println(g({2}))
