

type nat is (int x) where x >= 0

function inc([nat] xs) -> [nat]:
    int i = 0
    int j = 0
    while j < |xs| where i >= 0:
        if i < |xs|:
            xs[i] = xs[i] + 1
        i = i + 1
        j = j + 1
    assert no { x in xs | x < 0 }
    return xs

public export method test() -> void:
    assume inc([0]) == [1]
    assume inc([1, 2, 3]) == [2,3,4]
    assume inc([10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0]) == [11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1]
