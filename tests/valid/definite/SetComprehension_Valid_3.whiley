void System::main([string] args):
     xs = { 1,2,3,4 }
     ys = { 1,2 }
     zs = { x+y | x∈xs, y∈ys }
     out->println(str(xs))
     out->println(str(ys))
     out->println(str(zs))

