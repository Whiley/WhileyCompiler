void f([int] x) requires |x| > 0:
    y = x[0]
    z = x[0]
    assert y == z

void System::main([string] args):
    arr = [1,2,3]
    f(arr)
    out<->println(str(arr[0]))
