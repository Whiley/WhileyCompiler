type cr2num is (int x) where 1 <= x && x <= 4

function f(cr2num x) -> int:
    int y = x
    return y

public export method test() :
    assume f(3) == 3
