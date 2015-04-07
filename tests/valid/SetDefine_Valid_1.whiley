import whiley.lang.*

type pintset is ({int} xs) where |xs| > 1

method main(System.Console sys) -> void:
    {int} p = {1, 2}
    sys.out.println(p)
