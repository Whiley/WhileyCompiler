void System::main([string] args):
    xs = {1,2,3}
    zs = { x+y | x ∈ xs }
    this.out.println(str(xs))
    this.out.println(str(zs))
