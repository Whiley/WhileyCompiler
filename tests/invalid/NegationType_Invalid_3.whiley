import * from whiley.lang.*

type LinkedList is null | {LinkedList next, int data}

function f(LinkedList x) -> !null | int:
    return x

method main(System.Console sys) -> void:
    sys.out.println(Any.toString(f("Hello World")))
