type nat is (int n) where n >= 0

type natpair is (nat, int)

function min(natpair p) -> int:
    int x, int y = p
    if x > y:
        return y
    else:
        return x

method main() -> int:
    (int,int) p = (-1, 0)
    return min(p)
