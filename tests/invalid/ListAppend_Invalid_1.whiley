function f([int] r) -> int:
    return |r|

method g([real] args) -> int:
    [real|int] r = args ++ [1]
    return f(r)

