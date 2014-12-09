import * from whiley.lang.*

function f(System.Console sys, int x) -> void:
    sys.out.println("FIRST")

function f(System.Console sys, int x) -> void:
    sys.out.println("SECOND")

method main(System.Console sys) -> void:
    sys.out.println("NOUT")
