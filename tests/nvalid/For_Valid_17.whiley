import println from whiley.lang.System

type nat is int where $ >= 0

function sum({nat} xs) => int
ensures $ >= 0:
    r = 0
    for x in xs where r >= 0:
        r = r + x
    return r

method main(System.Console sys) => void:
    z = sum({1, 2, 3, 4, 5})
    sys.out.println(Any.toString(z))
