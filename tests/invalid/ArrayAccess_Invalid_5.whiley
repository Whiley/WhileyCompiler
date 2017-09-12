
function f(int[] x, int i) -> (bool r)
requires |x| > 0:
    if (i < 0) || (i >= |x|):
        i = 1
    int y = x[i]
    int z = x[i]
    assert y == z
    return true

public export method test():
    assume f([1,2], 1)
    assume f([1], 0)
    assume f([0;0], 1)
    
