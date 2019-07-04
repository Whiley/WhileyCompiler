type pos is (int x) where x >= 0
type neg is (int x) where x < 0

type R1 is {pos|neg f}

function f(int x) -> (R1 r)
requires x >= 0
ensures r == {f:x}:
    return {f: (pos) x}

public export method test():
    assert f(0) == {f:0}