
type scf4list is ([int] list) where |list| > 0

function f(scf4list x) -> int:
    return 1

method main() -> void:
    [int] x = []
    f(x)
