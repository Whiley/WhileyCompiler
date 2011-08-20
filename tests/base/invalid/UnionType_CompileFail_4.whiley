import whiley.lang.*:*

// this is a comment!
define IntRealList as [int]|[real]

[int] f([int] xs):
    return xs

void System::main([string] args):
    x = [1,2,3] // INT LIST
    ys = x      // OK
    this.out.println(str(ys))
    x[0] = 1.23 // NOT OK
    zs = f(x)
    this.out.println(str(zs))

