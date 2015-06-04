

type sr4set is {int}

public export method test() -> void:
    sr4set x = {1}
    assert x == {1}
