type arr is (int[] xs) where |xs| > 0

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
    arr a = select([1],[2],true)
    arr b = select(a,[2],false)
    assert a == [1]
    assert b == [2]
    // Pull cases
    assert select([1],[2],true) == [1]
    assert select([1],[2],false) == [2]
    assert select([1],b,true) == [1]
    assert select([1],b,false) == [2]
    assert select(a,[2],true) == [1]
    assert select(a,[2],false) == [2]
