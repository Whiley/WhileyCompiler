type pos is (int x) where x >= 0
type neg is (int x) where x < 0

type A1 is ((pos|neg)[] x)

function f(int x) -> (A1 r)
requires x >= 0
ensures r == [x]:
    return [(pos) x]

public export method test():
    assert f(0) == [0]