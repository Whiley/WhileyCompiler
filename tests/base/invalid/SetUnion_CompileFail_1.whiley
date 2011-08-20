import whiley.lang.*:*

void System::main([string] args):
    y = 1.0234234
    xs = {1,2,3,4}
    xs = xs ∪ y
    this.out.println(str(xs))
