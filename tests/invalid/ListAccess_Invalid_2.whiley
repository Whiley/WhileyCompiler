
function f(int[] x) -> void
requires |x| > 0:
    int y = x[0]
    int z = x[-1]
    assert y == z
