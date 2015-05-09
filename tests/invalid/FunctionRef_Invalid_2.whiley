function f(int x) -> int:
    return x + 1

function g(function func(int)->real) -> int:
    return func(1)

method h() -> int
    return g(&f)
