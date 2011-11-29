import * from whiley.lang.*

void f([int] x) requires |x| > 0:
    y = x[0]
    z = x[-1]
    assert y == z

void ::main(System sys,[string] args):
     arr = [1,2,3]
     f(arr)
     debug toString(arr[0])
