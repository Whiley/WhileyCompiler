

type nat is (int x) where x >= 0

function sum([nat] list) -> nat:
    int r = 0
    for l in list where r >= 0:
        assert r >= 0
        r = r + l
    return r

public export method test() -> void:
    nat rs = sum([0, 1, 2, 3])
    assert rs == 6
