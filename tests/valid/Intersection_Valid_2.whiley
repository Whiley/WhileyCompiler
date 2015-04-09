import whiley.lang.*

type LinkedList is null | {LinkedList next, int data}

type UnitList is {null next, int data}

type InterList is UnitList & LinkedList

function f(InterList l) -> int:
    return l.data

method main(System.Console sys) -> void:
    list = {next: null, data: 1234}
    assume f(list) == 1234
