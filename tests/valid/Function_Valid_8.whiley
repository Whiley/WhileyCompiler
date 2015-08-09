type fr6nat is (int x) where x >= 0

function g([fr6nat] xs) -> [fr6nat]:
    [fr6nat] ys = [0; |xs|]
    int i = 0
    while i < |xs| where i >= 0 && |xs| == |ys|:
        if xs[i] > 1:
            ys[i] = xs[i]
        i = i + 1
    return ys

function f([int] x) -> [int]:
    return x

public export method test() -> void:
    [int] ys = [1, 2, 3]
    assume f(g(ys)) == [0, 2, 3]

