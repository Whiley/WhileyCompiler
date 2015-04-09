import whiley.lang.*

function f([real] x) -> {int=>real}:
    return ({int=>real}) x

method main(System.Console sys) -> void:
    assume f([1.2, 2.3]) == {0=>1.2,1=>2.3}
