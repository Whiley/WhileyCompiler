type nat is (int x) where x >= 0

function f(int x) -> (nat r):
    nat y = (nat) x
    return y
