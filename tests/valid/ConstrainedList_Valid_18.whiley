

type nat is (int x) where x >= 0

function f([nat] xs) -> ([int] rs)
requires |xs| > 0
ensures some { x in rs | x >= 0 }:
    //
    return xs

public export method test() -> void:
    [int] rs = f([1, 2, 3])
    assume rs == [1,2,3]
