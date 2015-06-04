

function f([int] xs) -> [int]:
    return xs

public export method test() -> void:
    assume f([1, 4]) == [1,4]
    assume f([]) == []
