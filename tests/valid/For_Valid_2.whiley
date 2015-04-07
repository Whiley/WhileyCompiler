import whiley.lang.*

type nat is (int x) where x >= 0

function extract([int] ls) -> nat
requires |ls| > 0:
    for l in ls:
        if l < 0:
            return 0
    return ls[0]

method main(System.Console sys) -> void:
    nat rs = extract([-2, -3, 1, 2, -23, 3, 2345, 4, 5])
    sys.out.println(rs)
