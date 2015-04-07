import whiley.lang.*
import print from whiley.lang.*

method f(System.Console sys, int x) -> void:
    sys.out.println(x)

method main(System.Console sys) -> void:
    f(sys, 1)
