
type pintset is {int} where |$| > 1

function f(pintset x) => int:
    return |x|

method main(System.Console sys) => void:
    p = {1}
    debug Any.toString(p)
    f(p)
