

type sr3nat is (int n) where n > 0

public export method test() :
    int[] x = [2]
    x[0] = 1
    assert x == [1]
