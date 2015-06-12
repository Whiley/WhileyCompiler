type fr5nat is (int x) where x >= 0

function g([fr5nat] xs) -> [fr5nat]:
    [fr5nat] ys = []
    for y in xs:
        if y > 1:
            ys = ys ++ [y]
    return ys

function f([fr5nat] x) -> [int]:
    return x

public export method test() -> void:
    [int] ys = [1, 2, 3]
    assume f(g(ys)) == [2, 3]
