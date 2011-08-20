import whiley.lang.*:*

void System::main([string] args):
     xs = {1,2,3,4}
     ys = xs ∪ {5,1}
     this.out.println(str(xs))
     xs = xs ∪ {6}
     this.out.println(str(xs))
     this.out.println(str(ys))
