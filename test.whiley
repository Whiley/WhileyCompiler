define intlist as [int]|[[int]]

int f(intlist x):
    if x ~= [int]:
        return |x|
    return 1 // unreachable

void System::main([string] args):
    print str(f([1,2,3]))
