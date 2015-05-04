

type TYPE is null | int

function f([TYPE] xs, TYPE p) -> int:
    int r = 0
    for x in xs:
        if x == p:
            return r
        r = r + 1
    return -1

public export method test() -> void:
    assume f([null, 1, 2], null) == 0
    assume f([1, 2, null, 10], 10) == 3
