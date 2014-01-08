
type scf3nat is int where $ > 0

function f([scf3nat] xs) => int:
    return |xs|

method main(System.Console sys) => void:
    x = [1]
    x[0] = -1
    f(x)
