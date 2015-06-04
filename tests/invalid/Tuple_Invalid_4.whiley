type nat is (int n) where n >= 0

type natpair is (int, nat)

function min(natpair p) -> int:
    int x, int y = p
    if x > y:
        return y
    else:
        return x

method main() -> int:
    (int,int) p = (0, -1)
    return min(p)
