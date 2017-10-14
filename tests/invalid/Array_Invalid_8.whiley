function f(int[] xs) -> (int r)
requires |xs| > 0
ensures r < |xs|:
    //
    return 0

public export method test():
    //
    int[] xs = [0]
    xs[xs[f([0])]] = 1
    assert xs == [1]

