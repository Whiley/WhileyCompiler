import * from whiley.lang.*

function f(int x) => void:
    sys.out.println("FIRST")

function f(int x) => void:
    sys.out.println("SECOND")

method main(System.Console sys) => void:
    sys.out.println("NOUT")
