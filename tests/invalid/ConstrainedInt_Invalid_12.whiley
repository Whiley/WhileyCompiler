
type irf2nat is int x where x > 0

function f(irf2nat x) -> int:
    return x

function g(int x) -> void:
    f(x)

method main() -> void:
    g(-1)
