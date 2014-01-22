import println from whiley.lang.System

method main(System.Console sys) => void:
    l = [1, 2, 3]
    r = [4.23, 5.5]
    r = r + l
    sys.out.println(Any.toString(r))
