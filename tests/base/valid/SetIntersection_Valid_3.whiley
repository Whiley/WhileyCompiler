import whiley.lang.*:*

string f({int} xs):
    return str(xs)

string g({int} ys):
    return f(ys ∩ {1,2})

void System::main([string] args):
    this.out.println(g({}))
    this.out.println(g({2,3,4,5,6}))
    this.out.println(g({2,6}))
