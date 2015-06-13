

function test([real] xs, [int] ys) -> bool:
    [int|real] zs = xs ++ ys
    int i = 0
    while i < |zs|:
        if zs[i] is int:
            return true
        i = i + 1
    return false

public export method test() -> void:
    bool s = test([1.2, 2.3, 3.4], [1, 2, 3, 4, 5, 6, 7, 8])
    assume s == true    
    s = test([1.2, 2.3, 3.4], [])
    assume s == false
