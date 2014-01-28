import whiley.lang.System
import print from whiley.lang.System

method f(System.Console sys, int x) => void:
    sys.out.println(Any.toString(x))

method main(System.Console sys) => void:
    f(sys, 1)
    sys.out.print("")
