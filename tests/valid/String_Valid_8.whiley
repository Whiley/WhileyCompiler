import whiley.lang.System

method main(System.Console sys) => void:
    x = "abcdefghjkl"
    y = x[0..2]
    sys.out.println(y)
    y = x[1..3]
    sys.out.println(y)
    y = x[2..|x|]
    sys.out.println(y)
