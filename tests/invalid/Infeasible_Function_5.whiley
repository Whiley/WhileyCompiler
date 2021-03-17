function f() -> (int q)
ensures q >= 0 && q < 0:
    return -1

function g(int x) -> (int y)
ensures y >= 0:
    return x
