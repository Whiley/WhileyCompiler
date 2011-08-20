import whiley.lang.*:*

// this is a comment!
define num as {1,2,3,4}
define bignum as {1,2,3,4,5,6,7}

string f(num x):
    y = x
    return str(y)

string g({bignum} zs, int z):
    return f(z)

void System::main([string] args):
    this.out.println(g({1,2,3,5},3))
