

function f([int] ls) -> ([int] r)
ensures r == []:
    //
    if |ls| == 0:
        return ls
    else:
        return []

public export method test() -> void:
    [int] items = [5, 4, 6, 3, 7, 2, 8, 1]
    assume f(items) == []
    assume f([]) == []
