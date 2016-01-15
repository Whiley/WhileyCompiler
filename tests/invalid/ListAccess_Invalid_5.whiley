
function f(int[] x, int i) 
requires |x| > 0:
    if (i < 0) || (i >= |x|):
        i = 1
    int y = x[i]
    int z = x[i]
    assert y == z
