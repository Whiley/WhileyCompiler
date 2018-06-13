type pos is (int x) where x > 0
type nat is (int x) where x >= 0
type neg is (int x) where x < 0

function f(nat[] p) -> ((pos[])|(neg[]) r):
    return p
