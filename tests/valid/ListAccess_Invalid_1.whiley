import println from whiley.lang.System

void f([int] x) requires |x| > 0:
    y = x[0]
    z = x[0]
    assert y == z

void ::main(System.Console sys):
    arr = [1,2,3]
    f(arr)
    sys.out.println(Any.toString(arr[0]))
