void System::main([string] args):
    xs = {1,2,3}
    zs = { x+y | x ∈ xs }
    out->println(str(xs))
    out->println(str(zs))
