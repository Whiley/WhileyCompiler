

function f({int} xs) -> bool:
    if |xs| == 1:
        return true
    else:
        return false

function g({int} ys) -> bool:
    return f(ys + {1})

public export method test() -> void:
    assume g({}) == true
    assume g({2}) == false
