import whiley.lang.*
import whiley.lang.*

function toInt([int] ls) -> int:
    int r = 0
    for i in ls:
        r = r + i
    return r

public method main(System.Console sys) -> void:
    [int] ls = [1, 2, 3, 4]
    sys.out.println(toInt(ls))
