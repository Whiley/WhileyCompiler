import whiley.lang.*:*

string f({int} xs):
    return str(xs)

void System::main([string] args):
    this.out.println(f({1,4}))
    this.out.println(f({}))
    this.out.println(f(∅))
