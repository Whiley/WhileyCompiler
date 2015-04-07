import whiley.lang.*

type pintset is {int}

method main(System.Console sys) -> void:
    {int} p = {'b', 'c'}
    sys.out.println(p)
