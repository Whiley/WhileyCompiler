

type odd is (int x) where x in {1, 3, 5}

public export method test() -> void:
    odd y = 1
    assert y == 1
