string f({int} xs, {int} ys, {int} zs):
    return str(xs)

string g({int} ys):
    return f(ys,ys,ys)

string h({int} ys, {int} zs):
    return f(ys,zs,ys ∩ zs)

void System::main([string] args):
    out.println(g({}))
    out.println(g({2}))
    out.println(g({1,2,3}))
    out.println(h({},{}))
    out.println(h({1},{1,2}))
    out.println(h({1,2,3},{3,4,5}))
    out.println(h({1,2},{3,4,5}))
