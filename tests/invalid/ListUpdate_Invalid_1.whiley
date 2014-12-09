import whiley.lang.System

function f([int] xs) -> [int]:
    xs[0] = 1
    return xs

method main(System.Console sys) -> void:
    rs = f([])
    sys.out.println(Any.toString(rs))
