
function f(int[] x) 
requires |x| > 0:
    int y = x[0]
    int z = x[-1]
    assert y == z
