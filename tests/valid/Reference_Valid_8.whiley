method f(int x) -> (int|null r)
ensures r == x:
    &(int|null) ys = new(int|null) 1
    *ys = x
    return *ys

public export method test() :
    int|null result = f(6)
    assert result == 6
    