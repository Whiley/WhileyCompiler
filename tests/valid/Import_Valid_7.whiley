import whiley.lang.System
import whiley..*

function f(File.Reader r) => int:
    return 1

method main(System.Console sys) => void:
    x = 1
    y = 2
    sys.out.println(x + y)
