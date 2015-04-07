
function f(real x) -> real
requires x > 0.0:
    return 0.0

method main() -> void:
    real x = f(1.0)
    f(x)
