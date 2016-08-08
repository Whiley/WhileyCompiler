type c4nat is (int x) where x < 10

function h() -> (int r)
ensures r <= 5:
    //
    return 5

function f() -> c4nat:
    return h() * 2
