import whiley.lang.*

type pintset is {int}

method main(System.Console sys) -> void:
    {int} p = {'b', 'c'}
    assume p == {98, 99}

