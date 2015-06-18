type nat is (int x) where x >= 0

function sum([nat] list) -> nat:
    int r = 0
    int i = 0
    while i < |list| where r >= 0:
        assert r >= 0
        r = r + list[i]
        i = i + 1
    return r

public export method test() -> void:
    nat rs = sum([0, 1, 2, 3])
    assert rs == 6
