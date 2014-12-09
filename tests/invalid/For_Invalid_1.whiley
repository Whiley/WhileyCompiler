import * from whiley.lang.*

method main(System.Console sys) -> void:
    string st = "Hello World"
    for st in args:
        sys.out.println(st)
