

type num is (int x) where 1 <= x && x <= 4

type bignum is (int x) where 1 <= x && x <= 7

function f(num x) -> int:
    int y = x
    return y

function g([bignum] zs, int z) -> int:
    if (z in zs) && (z in [1,2,3,4]):
        return f(z)
    else:
        return -1

public export method test() -> void:
    assume g([1, 2, 3, 5], 3) == 3
