import whiley.lang.*:*

void System::main([string] args):
     xs = {1,2,3,4}
     xs = xs ∪ {5,1}
     this.out.println(str(xs))
