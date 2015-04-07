import whiley.lang.*

type nat is (int x) where x >= 0

function sum(int start, int end) -> nat:
    int r = 0
    for i in start .. end where r >= 0:
        r = r + 1
    return r

method main(System.Console sys) -> void:
    sys.out.println(sum(0, 10))
    sys.out.println(sum(10, 13))
