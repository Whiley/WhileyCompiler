import whiley.lang.*

function f(int x, int y) -> (int, int)
requires x == (2 * y):
    x = x + 2
    y = y + 1
    assert (2 * y) == x
    return (x, y)

public method main(System.Console console) -> void:
    assume f(2, 1) == (4,2)
