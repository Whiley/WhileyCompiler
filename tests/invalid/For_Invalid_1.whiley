import * from whiley.lang.*

method main(System.Console sys) => void:
    st = "Hello World"
    for st in args:
        sys.out.println(st)
