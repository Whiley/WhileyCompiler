import * from whiley.lang.*

method f(any this, string a) -> void:
    debug a

method main(System.Console sys) -> void:
    sys.out.println("HELLO")
    f(x,"WORLD")
