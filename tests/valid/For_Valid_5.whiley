

function f({(int, real)} xs, int m) -> real:
    for i, r in xs:
        if i == m:
            return r
    return -1

public export method test() -> void:
    x = f({(1, 2.2), (5, 3.3)}, 5)
    assume x == 3.3
    x = f({(1, 2.2), (5, 3.3)}, 2)
    assume x == -1.0
