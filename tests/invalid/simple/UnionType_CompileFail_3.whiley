// this is a comment!
define IntList as int|[int]

void System::main([string] args):
    IntList x
    int z
    if |args| == 0:
        int y
        x = 1
        y = x // OK
    else:
        [int] ys
        print str(y)
        x = [1,2,3]
        ys = x // OK
    z = x // should fail
    print str(z)

