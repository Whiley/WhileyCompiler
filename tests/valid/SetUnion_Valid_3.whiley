import whiley.lang.*

function test({real} xs, [int] ys) -> bool:
    for x in xs + ({int}) ys:
        if x == 3:
            return true
    return false

method main(System.Console sys) -> void:
    assume test({1.2, 2.3, 3.4}, [1, 2, 3, 4, 5, 6, 7, 8])
    assume !test({1.2, 2.3, 3.4}, [])
