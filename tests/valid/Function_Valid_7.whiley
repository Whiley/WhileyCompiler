type fr5nat is int

function g([fr5nat] xs) -> [fr5nat]:
    [fr5nat] ys = []
    int i = 0
    while i < |xs|:
        if xs[i] > 1:
            ys = ys ++ [xs[i]]
        i = i + 1
    return ys

function f([fr5nat] x) -> [int]:
    return x

public export method test() -> void:
    [int] ys = [1, 2, 3]
    assume f(g(ys)) == [2, 3]
