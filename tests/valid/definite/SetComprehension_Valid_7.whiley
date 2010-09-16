// this is a comment!
void f({int} xs) requires |xs| > 0:
    print str(xs)

void System::main([string] args):
    ys = {1,2,3}
    zs = {z | z in ys, z > 1}
    f(zs)
