type nat is (int x) where x >= 0
type pos is (nat x) where x > 0

function select<T>(T x, T y, bool f) -> (T z)
ensures f ==> (z == x)
ensures !f ==> (z == y):
    //
    if f:
        return x
    else:
        return y

public export method test():
    // Push cases
    nat a = select(1,2,true)
    nat b = select(a,2,false)
    assert a == 1
    assert b == 2
    // Pull cases
    assert select(1,2,true) == 1
    assert select(1,2,false) == 2
    assert select(1,b,true) == 1
    assert select(1,b,false) == 2
    assert select(a,2,true) == 1
    assert select(a,2,false) == 2
