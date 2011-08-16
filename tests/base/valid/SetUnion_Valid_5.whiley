string f({int} xs):
    if |xs| > 0:
        return str(xs)
    else:
        return "FAILED"

string g({int} ys):
    return f(ys ∪ {1})

void System::main([string] args):
    this.out.println(g({}))
    this.out.println(g({2}))
