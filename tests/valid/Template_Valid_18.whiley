function max(int x, int y) -> (int r)
ensures (r == x) || (r == y)
ensures (r >= x) && (r >= y):
    if x >= y:
        return x
    else:
        return y

function select<T>(int x, int y, T t1, T t2) -> (T r)
ensures (x >= y) ==> (r == t1)
ensures (x < y) ==> (r == t2):
    //
    if max(x,y) == x:
        return t1
    else:
        return t2

public export method test():
    &int p1 = new 8
    &int p2 = new 16
    //
    bool b1 = select(1,2,false,true)
    bool b2 = select(2,1,false,true)
    int  i1 = select(1,2,200,300)
    int  i2 = select(2,1,200,300)
    &int r1 = select(1,2,p1,p2)
    &int r2 = select(2,2,p1,p2)
    //
    assert b1
    assert !b2
    assert i1 == 300
    assert i2 == 200
    assert r1 == p2
    assert r2 == p1
    
    