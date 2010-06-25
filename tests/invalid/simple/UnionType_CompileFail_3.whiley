// this is a comment!
define int|[int] as IntList

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

