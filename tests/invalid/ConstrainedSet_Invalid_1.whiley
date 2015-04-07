
type pintset is ({int} xs) where |xs| > 1

function f(pintset x) -> int:
    return |x|

method main() -> int:
    {int} p = {1}
    return f(p)
