import * from whiley.lang.*

function g(int x) -> void:
    return

function f(int x, int y) -> int
requires (x >= 0) && (y > 0):
    g(z)
