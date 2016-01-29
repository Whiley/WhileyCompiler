

type fr3nat is (int x) where x >= 0

function f(int x) -> int:
    return x

public export method test() :
    int y = 1
    assume f(y) == 1
