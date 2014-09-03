import whiley.lang.System

function f([real] x) => {int=>real}:
    return ({int=>real}) x

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f([1.2, 2.3])))
