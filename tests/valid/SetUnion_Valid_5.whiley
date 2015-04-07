import whiley.lang.*

method main(System.Console sys) -> void:
    {int} xs = {1, 2, 3, 4}
    {int} ys = {5} + xs
    sys.out.println(xs)
    xs = xs + {6}
    sys.out.println(xs)
    sys.out.println(ys)
