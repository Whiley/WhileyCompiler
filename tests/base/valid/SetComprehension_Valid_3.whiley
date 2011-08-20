import whiley.lang.*:*

void System::main([string] args):
     xs = { 1,2,3,4 }
     ys = { 1,2 }
     zs = { x+y | x∈xs, y∈ys }
     this.out.println(str(xs))
     this.out.println(str(ys))
     this.out.println(str(zs))

