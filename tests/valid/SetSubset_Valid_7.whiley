import whiley.lang.*

function f({int} xs, {int} ys) -> bool
requires |xs| <= |ys|:
    if xs ⊂ ys:
        return true
    else:
        return false

method main(System.Console sys) -> void:
    sys.out.println(f({1, 2}, {1, 2, 3}))
    sys.out.println(f({1, 4}, {1, 2, 3}))
    sys.out.println(f({1}, {1, 2, 3}))
