string f({int} xs) requires |xs| < 3:
    return str(xs)

string g({int} ys):
    return f(ys ∩ {1,2})

void System::main([string] args):
    out<->println(g({}))
    out<->println(g({2,3,4,5,6}))
    out<->println(g({2,6}))
