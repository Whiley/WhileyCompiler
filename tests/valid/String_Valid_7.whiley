

function f([int] str, int end) -> [int]:
    return str[0..end]

public export method test() -> void:
    [int] str = "Hello Cruel World"
    assume f(str, 0) == ""
    assume f(str, 1) == "H"
    assume f(str, 5) == "Hello"
    assume f(str, 10) == "Hello Crue"
