import * from whiley.lang.*

function f(bool z) -> void:
    if z:
        for z in [1, 2, 3]:
            sys.out.println(Any.toString(z))
