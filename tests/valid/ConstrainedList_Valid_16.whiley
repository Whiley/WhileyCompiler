

type nat is (int x) where x >= 0

function f([int] xs) -> [nat]
requires |xs| == 0:
    return xs

public export method test() -> void:
    [nat] rs = f([])
    assume rs == []
