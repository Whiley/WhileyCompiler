

function f([int | real] e) -> [int]:
    if e is [int]:
        return e
    else:
        return [1, 2, 3]

public export method test() -> void:
    assume f([1, 2, 3, 4, 5, 6, 7]) == [1, 2, 3, 4, 5, 6, 7]
    assume f([]) == []
    assume f([1, 2, 2.01]) == [1,2,3]
    assume f([1.23, 2, 2.01]) == [1,2,3]
