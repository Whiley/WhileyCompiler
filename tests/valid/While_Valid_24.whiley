import whiley.lang.*

function indexOf([int] xs, int x) -> (int|null result)
// Either result is null, or gives the index of x in xs
ensures result is null || xs[result] == x:
    //
    int i = 0
    while i < |xs| where i >= 0:
        if xs[i] == x:
            return i
        i = i + 1
    return null

method main(System.Console console) -> void:
    console.out.println(indexOf([1, 2, 3], 1))
    console.out.println(indexOf([1, 2, 3], 0))
