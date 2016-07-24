function f(int[][] xs) -> (int[] rs)
requires |xs| > 0
requires |xs[0]| > 0:
    //
    xs[0][0] = xs[0][0]
    return xs[0]

public export method test():
    int[][] xs = [[0]]
    assume f(xs) == [0]

