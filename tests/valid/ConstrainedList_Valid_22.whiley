import whiley.lang.*

type nat is (int x) where x >= 0

function inc([nat] xs) -> [nat]:
    int i = 0
    for j in xs where i >= 0:
        if i < |xs|:
            xs[i] = xs[i] + 1
        i = i + 1
    assert no { x in xs | x < 0 }
    return xs

method main(System.Console sys) -> void:
    sys.out.println(inc([0]))
    sys.out.println(inc([1, 2, 3]))
    sys.out.println(inc([10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0]))
