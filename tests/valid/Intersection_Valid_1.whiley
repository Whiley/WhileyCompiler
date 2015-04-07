import whiley.lang.*

type EmptyList is [int] & [real]

function size(EmptyList l) -> int:
    return |l|

method main(System.Console sys) -> void:
    list = []
    sys.out.println(size(list))
