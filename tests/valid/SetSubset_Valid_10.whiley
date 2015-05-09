

function f({int} xs, {int} ys) -> bool:
    if xs ⊂ ys:
        return true
    else:
        return false

public export method test() -> void:
    assume f({1, 2, 3}, {1, 2, 3}) == false
    assume f({1, 2}, {1, 2, 3}) == true
    assume f({1}, {1, 2, 3}) == true
