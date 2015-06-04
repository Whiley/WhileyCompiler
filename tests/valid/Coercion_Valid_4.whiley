

function f([real] x) -> {int=>real}:
    return ({int=>real}) x

public export method test() -> void:
    assume f([1.2, 2.3]) == {0=>1.2,1=>2.3}
