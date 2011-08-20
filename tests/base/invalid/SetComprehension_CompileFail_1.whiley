import whiley.lang.*:*

void System::main([string] args):
    xs = { 1,2,3,4 }
    zs = { x | y ∈ xs }
    this.out.println(str(xs))
    this.out.println(str(zs))
