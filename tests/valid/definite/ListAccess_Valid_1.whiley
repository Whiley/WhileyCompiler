void f([int] x) requires |x| > 0:
    int y
    int z
    y = x[0]
    z = x[0]
    assert y == z

void System::main([string] args):
    [int] arr
    arr = [1,2,3]
    f(arr)
    print str(arr[0])
