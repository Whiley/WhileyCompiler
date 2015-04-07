type nat is (int x) where 1 <= x && x <= 8

function h() -> (int x)
ensures x <= 3:
    //
    return 0

function f() -> nat:
    return h()
