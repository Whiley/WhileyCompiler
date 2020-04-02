type nat is (int x) where x >= 0
type pos is (int x) where x > 0

function f(pos x) -> (nat|pos r)
ensures r == x:
    return x

public export method test():
    assert f(1) == 1