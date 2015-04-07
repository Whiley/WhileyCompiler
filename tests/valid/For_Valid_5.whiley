import whiley.lang.*

function f({(int, real)} xs, int m) -> real:
    for i, r in xs:
        if i == m:
            return r
    return -1

method main(System.Console sys) -> void:
    x = f({(1, 2.2), (5, 3.3)}, 5)
    sys.out.println(x)
    x = f({(1, 2.2), (5, 3.3)}, 2)
    sys.out.println(x)
