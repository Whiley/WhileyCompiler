import whiley.lang.System

function f(string str) => [int]:
    [int]|null r = null
    for i in str:
        if r == null:
            r = [0]
        else:
            r = r ++ [r[0]]
    return r

public method main(System.Console sys) => void:
    r = f("Hello")
    sys.out.println(r)
