function f(int x) -> int:
    return x + 1

function g(function func(real)->int) -> int:
    return func(1.2345)

method h() -> int
    return g(&f)
