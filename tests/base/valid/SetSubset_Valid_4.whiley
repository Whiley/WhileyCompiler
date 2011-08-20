import whiley.lang.*:*

string f({int} xs, {int} ys):
    if xs ⊂ ys:
        return "XS IS A SUBSET"
    else:
        return "FAILED"

void System::main([string] args):
    this.out.println(f({1,2,3},{1,2,3}))
    this.out.println(f({1,2},{1,2,3}))
    this.out.println(f({1},{1,2,3}))
