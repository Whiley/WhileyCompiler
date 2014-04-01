import whiley.lang.System

function f({int} xs, {int} ys) => string:
    if xs ⊂ ys:
        return "XS IS A SUBSET"
    else:
        return "FAILED"

method main(System.Console sys) => void:
    sys.out.println(f({1, 2, 3}, {1, 2, 3}))
    sys.out.println(f({1, 2}, {1, 2, 3}))
    sys.out.println(f({1}, {1, 2, 3}))
