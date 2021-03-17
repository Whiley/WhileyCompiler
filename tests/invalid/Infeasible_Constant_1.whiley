type nat is (int x) where x >= 0

nat x = -1

// This function should not be provable.
function f() -> (int r)
ensures r >= 0:
    return x
