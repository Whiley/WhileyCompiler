import whiley.lang.System

type pintset is {int}

method main(System.Console sys) => void:
    {char} p = {'b', 'c'}
    sys.out.println(Any.toString(p))
