import whiley.lang.*

method main(System.Console sys) -> void:
    int x = 1
    {int=>int} map = {1=>x, 3=>2}
    sys.out.println(map)
