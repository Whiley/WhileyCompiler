import println from whiley.lang.System

function binarySearch([int] items, int item) => bool
requires all { i in 0 .. (|items| - 1) | items[i] < items[i + 1] }
ensures !$ || some { i in items | i == item }
ensures $ || no { i in items | i == item }:
    lo = 0
    hi = |items|
    while lo < hi where (0 <= lo) && (hi <= |items|), where no { i in 0 .. lo | items[i] == item }, where no { i in hi .. |items| | items[i] == item }:
        mid = (lo + hi) / 2
        if items[mid] < item:
            lo = mid + 1
        else:
            if items[mid] > item:
                hi = mid
            else:
                return true
    return false

method main(System.Console console) => void:
    list = [3, 5, 6, 9]
    console.out.println(list)
    for i in 0 .. 10:
        console.out.println((i + " : ") + binarySearch(list, i))
