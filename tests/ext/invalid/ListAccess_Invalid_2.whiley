import * from whiley.lang.*

void f([int] x, int i) requires |x| > 0:
    if i < 0 || i >= |x|:
        i = 1
    y = x[i]
    z = x[i]
    assert y == z
    debug Any.toString(y)
    debug Any.toString(z)

void ::main(System.Console sys):
    arr = [1,2,3]
    f(arr, 1)
    debug Any.toString(arr)    
    f(arr, 2)
    debug Any.toString(arr)
    arr = [123]
    f(arr, 3)
    debug Any.toString(arr)
    arr = [123,22,2]
    f(arr, -1)
    debug Any.toString(arr)
    f(arr, 4)
