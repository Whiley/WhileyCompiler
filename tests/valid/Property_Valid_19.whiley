// Test of operator precedence
property test(bool flag, int y) -> (bool r):
    flag && y >= 0 ==> y < 10

function f(bool flag, int y) -> (int x)
requires test(flag,y)
ensures x == y:
    return y


public export method test():
    assert f(true,0) == 0
    assert f(false,0) == 0