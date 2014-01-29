
function sum({int} xs) => (int y)
ensures y >= 0:
    //
    int r = 0
    for x in xs where r >= 0:
        r = r + x
    return r

method main(System.Console sys) => void:
    int z = sum({-1, -2, -3, -4, 5})
    debug Any.toString(z)
