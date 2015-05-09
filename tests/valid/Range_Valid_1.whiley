

type nat is (int x) where x >= 0

function sum(int start, int end) -> nat:
    int r = 0
    for i in start .. end where r >= 0:
        r = r + 1
    return r

public export method test() -> void:
    assume sum(0, 10) == 10
    assume sum(10, 13) == 3
