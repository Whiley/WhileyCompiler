
function f(real x, int y) -> real
requires x >= y:
    return 0.0

method main(System.Console sys) -> void:
    real x = f(1.0, 1)
    f(x, 1)
