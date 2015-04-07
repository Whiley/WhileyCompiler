import whiley.lang.*
import toString from whiley.lang.Int

function f(int x) -> [int]:
    return toString(x)

import toString from whiley.lang.Real

function g(real x) -> [int]:
    return toString(x)

public method main(System.Console sys) -> void:
    sys.out.println_s("FIRST: " ++ f(1))
    sys.out.println_s("SECOND: " ++ g(1.2344))
