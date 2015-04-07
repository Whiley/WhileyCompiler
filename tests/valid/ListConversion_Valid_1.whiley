import whiley.lang.*

function f([real] ls) -> [real]:
    return ls

method main(System.Console sys) -> void:
    [int] ls = [1,2,3]
    sys.out.println(f(([real]) ls))
