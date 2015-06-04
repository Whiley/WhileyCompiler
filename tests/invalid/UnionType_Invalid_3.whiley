type IntList is int | [int]

function f([int] xs) -> int:
    return |xs|

function g(IntList xs) -> int:
    return f(xs)
