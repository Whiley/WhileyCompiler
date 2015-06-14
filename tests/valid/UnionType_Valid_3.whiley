

type TYPE is null | int

function f([TYPE] xs, TYPE p) -> int:
    int i = 0
    while i < |xs|:
        if xs[i] == p:
            return i
        i = i + 1
    return -1

public export method test() -> void:
    assume f([null, 1, 2], null) == 0
    assume f([1, 2, null, 10], 10) == 3
