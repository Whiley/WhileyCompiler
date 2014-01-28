import whiley.lang.System
import whiley..*

function f(File.Reader r) => int:
    return 1

method main(System.Console sys) => void:
    int x = 1
    int y = 2
    sys.out.println(x + y)
