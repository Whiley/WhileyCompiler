import whiley.lang.*

type R1 is {int x}

type R2 is {int y, int x}

function f(bool flag, [int] list) -> int:
    int r = 0
    if flag:
        for pos in list:
            r = r + pos
    else:
        for pos in list:
            r = r - pos
    return r

method main(System.Console sys) -> void:
    int r1 = f(true, [1, 2, 3, 4, 5, 6, 7, 8, 9, 10])
    int r2 = f(false, [1, 2, 3, 4, 5, 6, 7, 8, 9, 10])
    sys.out.println(r1)
    sys.out.println(r2)
