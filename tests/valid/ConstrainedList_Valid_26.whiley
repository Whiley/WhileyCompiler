import println from whiley.lang.System

function f([int] ls) => [int]
ensures $ == []:
    if |ls| == 0:
        return ls
    else:
        return []

method main(System.Console sys) => void:
    items = [5, 4, 6, 3, 7, 2, 8, 1]
    sys.out.println(f(items))
    sys.out.println(f([]))
