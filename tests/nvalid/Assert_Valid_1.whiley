import println from whiley.lang.System

function f(int | [int] xs) => int:
    if xs is [int]:
        y = 1
    else:
        y = 0
    if y == 1:
        assert xs is [int]
        return |xs|
    else:
        assert xs is int
        return xs

public method main(System.Console console) => void:
    console.out.println(f([1, 2, 3]))
    console.out.println(f(123456))
