type IntBoolList is int[] | bool[]

function f(int[] xs) -> int[]:
    return xs

method main() -> int[]:
    IntBoolList x = [1, 2, 3]
    x[0] = false
    return f(x)
