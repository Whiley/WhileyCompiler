type IntRealList is [int] | [real]

function f([int] xs) -> [int]:
    return xs

method main() -> [int]:
    IntRealList x = [1, 2, 3]
    x[0] = 1.23
    return f(x)
