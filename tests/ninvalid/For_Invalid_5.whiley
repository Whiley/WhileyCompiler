
function sum({int} xs) => {int}:
    rs = {}
    for x in xs where |rs| <= 2:
        rs = rs + {x}
    return rs

method main(System.Console sys) => void:
    z = sum({1, 2, 3, 4, 5})
    debug Any.toString(z)
