type nat is (int x) where x >= 0

function zero() -> int:
    int x = 0
    if x == 0:
        return x
    else:
        fail

public export method test() :
    assume zero() == 0
