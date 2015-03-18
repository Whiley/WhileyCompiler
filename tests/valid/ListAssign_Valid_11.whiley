import whiley.lang.System

function f([int] a) -> ASCII.string
// Input list cannot be empty
requires |a| > 0:
    //
    a[0] = 5
    return Any.toString(a)

method main(System.Console sys) -> void:
    [int] b = [1, 2, 3]
    sys.out.println(b)
    sys.out.println_s(f(b))
    sys.out.println(b)
