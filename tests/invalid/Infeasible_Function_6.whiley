function f(int x) -> (int q)
ensures q >= 0 && q < 0:
    return x

function g(int x) -> (int y)
ensures y >= 0:
    return x
