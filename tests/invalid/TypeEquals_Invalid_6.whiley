
type pos is (int x) where x > 0

type neg is (int x) where x < 0

type expr is pos | neg

function g(neg x) -> bool:
    return false

function f(expr e) -> void:
    if e is pos:
        g(e)

method main() -> void:
    f(-1)
    f(1)
