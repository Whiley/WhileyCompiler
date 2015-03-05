import whiley.lang.System
import toString from whiley.lang.Int

function f(int x) -> ASCII.string:
    return toString(x)

import toString from whiley.lang.Real

function g(real x) -> ASCII.string:
    return toString(x)

public method main(System.Console sys) -> void:
    sys.out.println("FIRST: " ++ f(1))
    sys.out.println("SECOND: " ++ g(1.2344))
