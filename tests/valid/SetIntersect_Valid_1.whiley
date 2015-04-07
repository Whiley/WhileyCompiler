import whiley.lang.*

method main(System.Console sys) -> void:
    {int} xs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
    sys.out.println(xs & {2, 3, 7, 11})
