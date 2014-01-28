import whiley.lang.System

type nat is (int x) where x >= 0

method main(System.Console sys) => void:
    xs = [1, 2, 3]
    r = 0
    for x in xs where r >= 0:
        r = r + x
    sys.out.println(Any.toString(r))
