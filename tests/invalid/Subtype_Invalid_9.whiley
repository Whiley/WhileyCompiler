
type scf9nat is int where $ > 0

type scf9tup is {scf9nat f, int g} where g > f

type scf9arr is [{scf9nat f, int g}] where some { z in $ | z.f == 1 }

function f(scf9arr xs) => int:
    return |xs|

method main(System.Console sys) => void:
    x = [{f: 1, g: 2}, {f: 4, g: 8}]
    x[0].f = 2
    f(x)
