import println from whiley.lang.System
import println from whiley.lang.System

function toInt([int] ls) => int:
    r = 0
    for i in ls:
        r = r + i
    return r

public method main(System.Console sys) => void:
    ls = [1, 2, 3, 4]
    s = Any.toString(toInt(ls))
    sys.out.println(s)
