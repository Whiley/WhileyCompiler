import whiley.lang.System

method main(System.Console sys) => void:
    {int|char} x = {1, 2, 3}
    {int|char} y = {1, 2}
    sys.out.println(x - y)
    x = {'a', 'b', 'c'}
    y = {'b', 'c'}
    sys.out.println(x - y)
