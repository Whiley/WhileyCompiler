
function f(real x) => real
requires x > 0:
    return 0.0

method main(System.Console sys) => void:
    real x = f(1.0)
    f(x)
