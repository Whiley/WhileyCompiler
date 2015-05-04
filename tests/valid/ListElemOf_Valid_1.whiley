

public export method test() -> void:
    [int] xs = [1, 2, 3, 3, 3, 4]
    assume 1 in xs
    assume !(5 in xs)
