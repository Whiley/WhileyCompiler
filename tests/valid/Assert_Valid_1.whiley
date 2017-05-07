function f(int[] xs) -> (int[] ys)
requires |xs| > 0
ensures xs[0] == 0:
    //
    xs[0] = 0
    //
    assert some { k in 0..|xs| | xs[k] == 0 }
    //
    return xs

//
method test():
    int[] xs = f([1])
    //
    assert f(xs)[0] == 0