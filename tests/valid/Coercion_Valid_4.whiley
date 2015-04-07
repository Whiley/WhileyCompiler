import whiley.lang.*

function f([real] x) -> {int=>real}:
    return ({int=>real}) x

method main(System.Console sys) -> void:
    sys.out.println(f([1.2, 2.3]))
