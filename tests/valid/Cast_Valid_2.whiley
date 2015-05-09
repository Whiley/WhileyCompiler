

function f([int] xs) -> [real]:
    return ([real]) xs

public export method test() -> void:
    assume f([1, 2, 3]) == [1.0, 2.0, 3.0]
