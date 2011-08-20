import whiley.lang.*:*

// this is a comment!
string f({int} xs):
    return str(xs)

void System::main([string] args):
    ys = {1,2,3}
    zs = ys
    this.out.println(f(zs))
