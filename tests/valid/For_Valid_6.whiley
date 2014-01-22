import println from whiley.lang.System

function select({int} xs) => int
requires |xs| > 0
ensures $ in xs:
    for x in xs:
        return x
    return 0

method main(System.Console sys) => void:
    sys.out.println(select({1, 2, 3, 4, 5, 6, 7, 8, 9, 10}))
