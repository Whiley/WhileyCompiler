
type pintset is ({int} xs) where |xs| > 1

function f(pintset x) => int:
    return |x|

method main(System.Console sys) => void:
    {int} p = {1}
    debug Any.toString(p)
    f(p)
