import whiley.lang.System

function f({int} xs, {int} ys) -> ASCII.string:
    if xs ⊂ ys:
        return "XS IS A SUBSET"
    else:
        return "XS IS NOT A SUBSET"

method main(System.Console sys) -> void:
    sys.out.println_s(f({1, 2}, {1, 2, 3}))
    sys.out.println_s(f({1, 4}, {1, 2, 3}))
    sys.out.println_s(f({1}, {1, 2, 3}))
