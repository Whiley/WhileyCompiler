import whiley.lang.*

type pintset is {int}

method main(System.Console sys) -> void:
    {int} p = {1, 2}
    assert p == {1,2}
