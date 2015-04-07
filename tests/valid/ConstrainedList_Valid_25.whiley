import whiley.lang.*

function indexOf([int] list, int index) -> int
requires all { l in list | l >= 0 } && ((index >= 0) && (index < |list|)):
    return list[index]

method main(System.Console sys) -> void:
    [int] items = [5, 4, 6, 3, 7, 2, 8, 1]
    for i in 0 .. |items|:
        sys.out.println(indexOf(items, i))
