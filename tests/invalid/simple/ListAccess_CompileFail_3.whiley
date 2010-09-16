define intList as int|[int]
define tup as {int mode, intList data}

[{int mode, int data}] f([{int mode, int data}] x):
    return x

void System::main([string] args):
    tups = [{mode:0,data:1},{mode:1,data:[1,2,3]}]
    [{int mode, int data}] btups
    tups[0].data = 1
    tups = f(tups) // NOT OK
    print str(tups)

