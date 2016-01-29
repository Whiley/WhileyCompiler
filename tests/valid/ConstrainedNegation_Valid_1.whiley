
type nat is (int x) where x >= 0
type neg is !nat

function f(neg x) -> (int y)
ensures y < 0:
    if x is int:
        return x
    else:
        return 0

public export method test() :
    assume f(-1) == -1
    assume f(-2) == -2
