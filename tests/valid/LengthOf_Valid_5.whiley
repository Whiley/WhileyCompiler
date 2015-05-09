

type list is [int]

function len(list l) -> int:
    return |l|

public export method test() -> void:
    [int] l = [1, 2]
    assume len(l) == 2
    [int] s = "Hello World"
    assume len(s) == 11
