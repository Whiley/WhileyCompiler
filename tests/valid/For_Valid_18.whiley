import whiley.lang.*

type nat is (int n) where 0 >= n

function sum({nat} xs) -> int:
    int r = 0
    for x in xs:
        r = r + x
    return r

method main(System.Console sys) -> void:
    int z = sum({1, 2, 3, 4, 5})
    sys.out.println(z)
