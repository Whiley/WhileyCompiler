

type list is [int]

function f(list ls) -> int:
    int r = 0
    for l in ls:
        r = r + ((int)l)
    return r

public export method test() -> void:
    [int] ls = [1, 2, 3, 4, 5, 6, 7, 8]
    assume f(ls) == 36
    [int] xs = "Hello World"
    assume f(xs) == 1052
