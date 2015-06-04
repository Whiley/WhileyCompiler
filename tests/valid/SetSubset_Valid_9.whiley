

function f({int} xs, {int} ys) -> bool
requires xs ⊂ ys:
    //
    return true

public export method test() -> void:
    assume f({1, 2}, {1, 2, 3}) == true
    assume f({1}, {1, 2, 3}) == true
