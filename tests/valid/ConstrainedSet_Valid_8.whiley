

type pintset is {int}

public export method test() -> void:
    pintset p = {1, 2}
    assert p == {1,2}
