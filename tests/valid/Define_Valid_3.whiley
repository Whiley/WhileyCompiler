

type odd is (int x) where x == 1 || x == 3 || x == 5

public export method test() :
    odd y = 1
    assert y == 1
