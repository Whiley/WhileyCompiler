import whiley.lang.System

type pintset is {int}

method main(System.Console sys) -> void:
    {ASCII.char} p = {'b', 'c'}
    sys.out.println(p)
