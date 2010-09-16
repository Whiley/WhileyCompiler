void System::main([string] args):
     xs = { 1,2,3,4 }
     ys = { 1,2 }
     zs = { x+y | x∈xs, y∈ys, x!=y }
     print str(xs)
     print str(ys)
     print str(zs)
