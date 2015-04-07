import whiley.lang.*

method main(System.Console sys) -> void:
    {int} x = {1, 2, 3}
    {int} y = {1, 2}
    sys.out.println(x - y)
    x = {'a', 'b', 'c'}
    y = {'b', 'c'}
    sys.out.println(x - y)
