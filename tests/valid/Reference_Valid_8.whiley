method f(int x) -> (int|null r):
    &(int|null) ys = new(int|null) 1
    *ys = x
    return *ys

public export method test() :
    int|null result = f(6)
    assume result == 6
    