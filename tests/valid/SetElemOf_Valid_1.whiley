import whiley.lang.*

method main(System.Console sys) -> void:
    {int} xs = {1, 2, 3}
    assert 1 in xs
    assert !(5 in xs)
