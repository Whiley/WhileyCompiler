import * from whiley.lang.*

method f(System this, string a) => void:
    sys.out.println(a)

method main(System.Console sys) => void:
    sys.out.println("HELLO")
    x.f("WORLD")
