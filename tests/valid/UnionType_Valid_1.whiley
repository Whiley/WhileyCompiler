import whiley.lang.System

method main(System.Console sys) => void:
    if |sys.args| == 1:
        x = 1
    else:
        x = [1, 2, 3]
    sys.out.println(Any.toString(x))
