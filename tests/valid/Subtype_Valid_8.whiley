

type sr4set is ({int} xs) where |xs| > 0

public export method test() -> void:
    sr4set x = {1}
    assert x == {1}
