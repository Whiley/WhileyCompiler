import whiley.lang.System
import nat from whiley.lang.Int

function sum({nat} xs) => int:
    int r = 0
    for x in xs:
        r = r + x
    return r

method main(System.Console sys) => void:
    int z = sum({1, 2, 3, 4, 5})
    sys.out.println(Any.toString(z))
