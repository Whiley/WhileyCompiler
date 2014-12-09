import * from whiley.lang.*

type R1 is {real x}

function f([real] xs) -> [int]:
    return ([real]) xs

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(f([1.0, 2.0, 3.0])))
