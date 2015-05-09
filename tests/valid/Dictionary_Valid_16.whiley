

function f(int x) -> {int=>int}:
    return {1=>x, 3=>2}

public export method test() -> void:
    assume f(1) == {1=>1, 3=>2}
    assume f(2) == {1=>2, 3=>2}
    assume f(3) == {1=>3, 3=>2}
