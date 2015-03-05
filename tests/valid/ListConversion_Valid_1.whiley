import whiley.lang.System

function f([real] ls) -> ASCII.string:
    return Any.toString(ls)

method main(System.Console sys) -> void:
    [int] ls = [1,2,3]
    sys.out.println(f(([real]) ls))
