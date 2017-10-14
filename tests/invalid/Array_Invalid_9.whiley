function f(int[] xs) -> (int r)
requires |xs| > 0
ensures r < |xs|:
    //
    return 0

method g(int x) -> (int r)
requires x >= 0:
    return x

public export method test():
    //
    g(f([0]))

