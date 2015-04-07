import whiley.lang.*

type R1 is {real x}

function f([int] xs) -> [real]:
    return ([real]) xs

method main(System.Console sys) -> void:
    sys.out.println(f([1, 2, 3]))
