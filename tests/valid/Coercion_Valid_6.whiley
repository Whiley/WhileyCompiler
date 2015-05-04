

function f([real] x) -> {real}:
    return ({real}) x

public export method test() -> void:
    assume f([2.2, 3.3]) == {2.2,3.3}
