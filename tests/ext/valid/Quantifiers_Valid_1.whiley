// this is a comment!
string f({int} xs) requires no { w in xs | w < 0}:
    return str(xs)

void System::main([string] args):
    ys = {1,2,3}
    zs = ys
    this.out.println(f(zs))
