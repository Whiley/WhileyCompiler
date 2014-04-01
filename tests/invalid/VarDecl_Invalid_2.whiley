
function f(int x) => void
requires x >= 0:
    int y = 10 / x
    debug Any.toString(x)
    debug Any.toString(y)

method main(System.Console sys) => void:
    f(10)
    f(0)
