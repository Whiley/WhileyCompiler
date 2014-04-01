import whiley.lang.System

function f({int} xs, {int} ys) => string
requires |xs| <= |ys|:
    if xs ⊆ ys:
        return "XS IS A SUBSET"
    else:
        return "XS IS NOT A SUBSET"

method main(System.Console sys) => void:
    sys.out.println(f({1, 2, 3}, {1, 2, 3}))
    sys.out.println(f({1, 4}, {1, 2, 3}))
    sys.out.println(f({1}, {1, 2, 3}))
