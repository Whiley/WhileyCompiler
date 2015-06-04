

type pintset is ({int} xs) where |xs| > 1

public export method test() -> void:
    pintset p = {1, 2}
    assume p == {1,2}
