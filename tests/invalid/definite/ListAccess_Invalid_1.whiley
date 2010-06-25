void f([int] x):
    int y
    int z
    y = x[0]
    z = x[1]
    assert y == z

void System::main([string] args):
    [int] arr 
    arr = [1,2,3]
    f(arr)
