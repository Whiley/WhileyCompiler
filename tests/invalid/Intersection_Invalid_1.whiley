import * from whiley.lang.*

type EmptyList is [int] & [real]

function size(EmptyList l) -> int:
    return |l|

method main(System.Console sys) -> void:
    list = [1]
    sys.out.println(Any.toString(size(list)))
