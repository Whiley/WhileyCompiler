import whiley.lang.*:*

string f({int} xs, {int} ys, {int} zs):
    return str(xs)

string g({int} ys):
    return f(ys,ys,ys)

string h({int} ys, {int} zs):
    return f(ys,zs,ys ∩ zs)

void System::main([string] args):
    this.out.println(g({}))
    this.out.println(g({2}))
    this.out.println(g({1,2,3}))
    this.out.println(h({},{}))
    this.out.println(h({1},{1,2}))
    this.out.println(h({1,2,3},{3,4,5}))
    this.out.println(h({1,2},{3,4,5}))
