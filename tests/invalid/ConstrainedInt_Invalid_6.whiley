
constant c3num is {1, 2, 3, 4}

function f(c3num x) => void:
    int y = x
    debug Any.toString(y)

function g(int z) => void:
    f(z)

method main(System.Console sys) => void:
    g(5)
