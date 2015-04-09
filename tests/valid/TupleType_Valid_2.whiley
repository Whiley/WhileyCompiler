import whiley.lang.*

function f(int x) -> (int, int):
    return (x, x + 2)

method main(System.Console sys) -> void:
    (int,int) x = f(1)
    assume x == (1,3)
