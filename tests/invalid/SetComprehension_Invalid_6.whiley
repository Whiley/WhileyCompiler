
function pred({int} xs) => ({int} ys)
ensures no { z in ys | z < 0 }:
    //
    zs = { z | z in xs, z < 0 }
    return zs

method main(System.Console sys) => void:
    pred({-1, 0, 1})
