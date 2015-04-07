import whiley.lang.*

type rec is {int y, int x}

function f([int] xs) -> [bool | null]:
    [bool|null] r = []
    for x in xs:
        if x < 0:
            r = r ++ [true]
        else:
            r = r ++ [null]
    return r

method main(System.Console sys) -> void:
    [int] e = []
    sys.out.println(f(e))
    e = [1, 2, 3, 4]
    sys.out.println(f(e))
