import whiley.lang.*

method main(System.Console sys) -> void:
    {int} xs = {1, 2, 3, 4}
    xs = xs + {5, 1}
    assume xs == {1,2,3,4,5}
