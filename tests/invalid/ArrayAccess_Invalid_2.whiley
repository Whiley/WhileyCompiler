
function f(int[] x) -> bool 
requires |x| > 0:
    int y = x[0]
    int z = x[-1]
    assert y == z
    return true

public export method test():
    assume f([1,2])
