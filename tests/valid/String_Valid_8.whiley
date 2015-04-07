import whiley.lang.*

method main(System.Console sys) -> void:
    [int] x = "abcdefghjkl"
    [int] y = x[0..2]
    sys.out.println_s(y)
    y = x[1..3]
    sys.out.println_s(y)
    y = x[2..|x|]
    sys.out.println_s(y)
