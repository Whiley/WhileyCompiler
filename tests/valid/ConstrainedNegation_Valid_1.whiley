
type nat is (int x) where x >= 0
type neg is (int x) where x < 0

function f(int x) -> (int y)
ensures y < 0:
    if x is neg:
        return x
    else:
        return -1

public export method test() :
    assume f(-1) == -1
    assume f(-2) == -2
