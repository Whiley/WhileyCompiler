

function f([int] str) -> [int]:
    [int]|null r = null
    for i in str:
        if r == null:
            r = [0]
        else:
            r = r ++ [r[0]]
    return r

public export method test() -> void:
    assumr f("Hello") == [0,0,0,0,0]
