
type scf4set is {int} where |$| > 0

function f(scf4set x) => int:
    return 1

method main(System.Console sys) => void:
    x = {}
    f(x)
