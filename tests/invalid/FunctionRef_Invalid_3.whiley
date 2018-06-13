function f(int x) -> int:
    return x + 1

function g(function func(bool)->int) -> int:
    return func(0b010)

method h() -> int
    return g(&f)
