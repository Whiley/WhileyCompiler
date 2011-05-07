// this is a comment!
string f({int} xs) requires |xs| > 0:
    return str(xs)

void System::main([string] args):
    ys = {1,2,3}
    zs = {z | z in ys, z > 1}
    out<->println(f(zs))
