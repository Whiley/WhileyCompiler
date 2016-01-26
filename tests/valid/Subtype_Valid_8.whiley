

type sr4list is (int[] xs) where |xs| > 0

public export method test() :
    sr4list x = [1]
    assert x == [1]
