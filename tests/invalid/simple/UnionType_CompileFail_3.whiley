// this is a comment!
define IntList as int|[int]

int f([int] xs):
    return |xs|

void System::main([string] args):
    if |args| == 0:
        x = 1
        y = x // OK
    else:
        print str(y)
        x = [1,2,3]
        ys = x // OK
    z = f(x) // should fail
    print str(z)

