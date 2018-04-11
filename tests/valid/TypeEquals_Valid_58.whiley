type nat is (int n) where n >= 0

function f(int|{int f}|{int|null f} x) -> (int r)
ensures x is {int f} ==> (r == x.f):
    //
    if x is {int f}:
        return x.f
    else:
        return 0

public export method test():
    //
    {int|null f} r1 = {f:123}
    {int|null f} r2 = {f:null}
    {int f} r3 = {f:124}    
    //
    assert f(r1) == 123
    assert f(r2) == 0
    assert f(r3) == 124
    assert f(0) == 0
    //