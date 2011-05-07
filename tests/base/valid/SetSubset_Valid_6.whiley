string f({int} xs, {int} ys):
    if xs ⊂ ys:
        return "XS IS A SUBSET"
    else:
        return "FAILED"

string g({int} xs, {int} ys):
    return f(xs,ys)

void System::main([string] args):
    out<->println(g({1,2,3},{1,2,3}))
    out<->println(g({1,2},{1,2,3}))
    out<->println(g({1},{1,2,3}))
