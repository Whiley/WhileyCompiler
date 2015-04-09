import whiley.lang.*

function f(int x) -> int:
    int y
    switch x:
        case 1:
            y = -1
        case 2:
            y = -2
        default:
            y = 0
    return y

method main(System.Console sys) -> void:
    assume f(1) == -1
    assume f(2) == -2
    assume f(3) == 0
    assume f(-1) == 0
