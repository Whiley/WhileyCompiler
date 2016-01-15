

type pos is (int x) where x > 0

type neg is (int x) where x < 0

type posneg is pos | neg

function isPosNeg(any v) -> bool:
    if v is posneg:
        return true
    else:
        return false

public export method test() :
    assume isPosNeg(1)
    assume !isPosNeg(0)
    assume isPosNeg(-1)
