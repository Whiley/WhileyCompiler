import whiley.lang.*

type nat is (int x) where x >= 0

function sum({nat} xs) -> (int result)
ensures result >= 0:
    //
    int r = 0
    for x in xs where r >= 0:
        r = r + x
    return r

method main(System.Console sys) -> void:
    int z = sum({1, 2, 3, 4, 5})
    sys.out.println(z)
