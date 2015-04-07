import whiley.lang.*
import nat from whiley.lang.Int

type nlist is nat | [int]

function f(int x) -> nlist:
    if x <= 0:
        return 0
    else:
        return f(x - 1)

method main(System.Console sys) -> void:
    nlist x = f(2)
    sys.out.println(x)
