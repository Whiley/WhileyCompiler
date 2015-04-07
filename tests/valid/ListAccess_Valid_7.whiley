import whiley.lang.*

function f([int] str) -> [int]:
    [int]|null r = []
    for i in str:
        if r == null:
            r = [0]
        else:
            if i == ' ':
                r = null
            else:
                r = r + [r[0]]
    return r

public method main(System.Console sys) -> void:
    r = f("Hello")
    sys.out.println(r)
