

type posints is ({int} xs) where no { x in xs | x < 0 }

function f(posints x) -> {int}:
    return x

public export method test() -> void:
    posints xs = {1, 2, 3}
    assume f(xs) == {1,2,3}
