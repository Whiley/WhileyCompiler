import whiley.lang.*:*

string f({int} xs) requires |xs| > 0:
    return str(xs)

string g({int} ys):
    return f(ys ∪ {1})

void System::main([string] args):
    this.out.println(g({}))
    this.out.println(g({2}))
