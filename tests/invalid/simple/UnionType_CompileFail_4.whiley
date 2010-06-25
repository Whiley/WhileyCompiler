// this is a comment!
define [int]|[real] as IntRealList

void System::main([string] args):
    IntRealList x
    [int] ys
    [real] zs
    x = [1,2,3] // INT LIST
    ys = x      // OK
    print str(ys)
    x[0] = 1.23 // NOT OK
    zs = x      
    print str(zs)

