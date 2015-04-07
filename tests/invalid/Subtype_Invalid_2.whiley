
type scf2nat is (int x) where x >= 0

function f(scf2nat x) -> void:
    x = -1
    f(x)

method main() -> void:
    f(1)
