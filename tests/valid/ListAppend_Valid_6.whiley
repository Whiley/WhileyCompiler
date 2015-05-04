

function test([real] xs, [int] ys) -> bool:
    for x in xs ++ ys:
        if x is int:
            return true
    return false

public export method test() -> void:
    bool s = test([1.2, 2.3, 3.4], [1, 2, 3, 4, 5, 6, 7, 8])
    assume s == true    
    s = test([1.2, 2.3, 3.4], [])
    assume s == false
