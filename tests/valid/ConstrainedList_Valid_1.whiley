import whiley.lang.System
import * from whiley.lang.Int

function f(int x) => [u8]
requires (x == 0) || (x == 169):
    return [x]

method main(System.Console sys) => void:
    [u8] bytes = f(0)
    sys.out.println(Any.toString(bytes))
