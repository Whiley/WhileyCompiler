

function indexOf([int] list, int index) -> int
requires all { l in list | l >= 0 } && ((index >= 0) && (index < |list|)):
    return list[index]

public export method test() -> void:
    [int] items = [5, 4, 6, 3, 7, 2, 8, 1]
    int i = 0
    while i < |items|:
        assume indexOf(items,i) == items[i]
        i = i + 1
