function f(int[] xs) -> (int r)
ensures r >= 0:
    assume |xs| > 0 && xs[0] >= 0
    return xs[0]

//
public export method test():
    int[] xs = [1,2,3]
    int x = f(xs)
    assert x >= 0