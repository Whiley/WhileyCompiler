import * from whiley.lang.*

method main(System.Console sys) -> void:
    real y = 1.0234234
    {int} xs = {1, 2, 3, 4}
    xs = xs + y
    sys.out.println(Any.toString(xs))
