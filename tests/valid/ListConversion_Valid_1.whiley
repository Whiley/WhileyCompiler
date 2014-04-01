import whiley.lang.System

function f([real] ls) => string:
    return Any.toString(ls)

method main(System.Console sys) => void:
    sys.out.println(f([1, 2, 3]))
