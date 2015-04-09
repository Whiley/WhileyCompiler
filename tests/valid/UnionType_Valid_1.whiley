import whiley.lang.*

method main(System.Console sys) -> void:
    int|[int] x
    //
    if |sys.args| == 1:
        x = 1
    else:
        x = [1, 2, 3]
    //
    assume x == [1,2,3]
