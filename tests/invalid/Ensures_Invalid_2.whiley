
method g(int x) => int:
    return x + 1

method f(int x) => void
requires x > g(x):
    debug Any.toString(x)

method main(System.Console sys) => void:
    f(1)
