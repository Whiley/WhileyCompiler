

function f([int] str) -> [int]:
    [int]|null r = []
    for i in str:
        if r == null:
            r = [0]
        else:
            if i == ' ':
                r = null
            else:
                r = r + [r[0]]
    return r

public export method test() -> void:
    assume f("Hello") == [0,0,0,0,0]
