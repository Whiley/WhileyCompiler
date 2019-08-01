type nat is (int x) where x >= 0
type dummy is (nat y) where y < 5

function f(int x) -> (int r)
// General case
ensures r >= 0 && r <= 2
// Dummy case
ensures (x >= 0 && x < 5) <==> (r == 2)
// nat case
ensures (x >= 5) <==> (r == 1):
    //
    if x is dummy:
        return 2
    else if x is nat:
        return 1
    else:
        return 0

public export method test():
    assert f(-2) == 0
    assert f(-1) == 0
    assert f(0) == 2
    assert f(1) == 2
    assert f(2) == 2
    assert f(3) == 2
    assert f(4) == 2
    assert f(5) == 1
    assert f(6) == 1
    assert f(7) == 1    