type nat is (int x) where x >= 0

function sum(int start, int end) -> nat:
    int r = 0
    [int] xs = start .. end
    int i = 0
    while i < |xs| where r >= 0:
        r = r + 1
        i = i + 1
    return r

public export method test() -> void:
    assume sum(0, 10) == 10
    assume sum(10, 13) == 3
