
function g(real x) => real
requires x <= 0.5
ensures $ < 0.16:
    return x / 3.0

method main(System.Console sys) => void:
    debug Any.toString(g(0.234))
    debug Any.toString(g(0.5))
