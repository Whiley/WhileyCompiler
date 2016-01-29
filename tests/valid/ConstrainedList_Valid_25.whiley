

function indexOf(int[] list, int index) -> int
requires all { i in 0..|list| | list[i] >= 0 }
requires index >= 0 && index < |list|:
    return list[index]

public export method test() :
    int[] items = [5, 4, 6, 3, 7, 2, 8, 1]
    int i = 0
    while i < |items|:
        assume indexOf(items,i) == items[i]
        i = i + 1
