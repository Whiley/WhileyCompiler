// this is a comment!
define {1,2,3,4} as num
define {1,2,3,4,5,6,7} as bignum

void f(num x):
    num y
    y = x
    print str(y)

void g({bignum} zs, int z) requires z in {x | x in zs, x < 6}:
    f(z)

void System::main([string] args):
    g({1,2,3,5},5)
