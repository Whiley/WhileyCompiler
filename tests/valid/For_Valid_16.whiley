import whiley.lang.System

type nat is int

method main(System.Console sys) => void:
    [int] xs = [1, 2, 3]
    int r = 0
    for x in xs:
        r = r + x
    sys.out.println(Any.toString(r))
