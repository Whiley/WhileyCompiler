type pos is (int x) where x >= 0
type neg is (int x) where x < 0

type A1 is ((pos|neg)[] x)

function f() -> (A1 r):
    return [0,1,2]