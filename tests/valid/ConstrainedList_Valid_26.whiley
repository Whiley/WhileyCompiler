import whiley.lang.*

function f([int] ls) -> ([int] r)
ensures r == []:
    //
    if |ls| == 0:
        return ls
    else:
        return []

method main(System.Console sys) -> void:
    [int] items = [5, 4, 6, 3, 7, 2, 8, 1]
    sys.out.println(f(items))
    sys.out.println(f([]))
