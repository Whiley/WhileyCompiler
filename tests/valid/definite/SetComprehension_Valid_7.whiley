// this is a comment!
void f({int} xs) where |xs| > 0:
    print str(xs)

void System::main([string] args):
    {int} ys
    {int} zs
    ys = {1,2,3}
    zs = {z | z in ys, z > 1}
    f(zs)
