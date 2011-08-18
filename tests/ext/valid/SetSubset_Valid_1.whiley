string f({int} xs, {int} ys) requires |xs| <= |ys|:
    if xs ⊆ ys:
        return "XS IS A SUBSET"
    else:
        return "XS IS NOT A SUBSET"

void System::main([string] args):
    this.out.println(f({1,2,3},{1,2,3}))
    this.out.println(f({1,4},{1,2,3}))
    this.out.println(f({1},{1,2,3}))
