import whiley.lang.System

function isChar(any x) => bool:
    if x is char:
        return true
    else:
        return false

method main(System.Console sys) => void:
    sys.out.println(Any.toString(isChar('c')))
    sys.out.println(Any.toString(isChar(1)))
    sys.out.println(Any.toString(isChar([1, 2, 3])))
