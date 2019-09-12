type IntBoolList is int[] | bool[]

function f(int[] xs) -> int[]:
    return xs

public export method test() -> int[]:
    IntBoolList x = (IntBoolList) [1, 2, 3]
    x[0] = false
    IntBoolList y = [1, 2, 3]
    y[0] = false
    return f(x)
