import println from whiley.lang.System

function indexOf([int] xs, int x) => int | null
ensures ($ is null) || (xs[$] == x):
    i = 0
    while i < |xs| where i >= 0:
        if xs[i] == x:
            return i
        i = i + 1
    return null

method main(System.Console console) => void:
    console.out.println(indexOf([1, 2, 3], 1))
    console.out.println(indexOf([1, 2, 3], 0))
