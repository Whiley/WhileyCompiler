import whiley.lang.*
import nat from whiley.lang.Int

type nlist is int | [nat]

function f(int i, [nlist] xs) -> nlist:
    if (i < 0) || (i >= |xs|):
        return 0
    else:
        return xs[i]

method main(System.Console sys) -> void:
    nlist x = f(2, [2, 3, 4])
    sys.out.println(x)
