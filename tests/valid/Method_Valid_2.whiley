method get(int[] xs, int i) -> (int r)
requires 0 <= i && i < |xs|:
    return xs[i]


public export method test():
    int i = 0
    int[] xs = [1,2,3,0,2]
    //
    while i < |xs| && get(xs,i) != 0
    where i >= 0:
        i = i + 1
    //
    assume i == 3
