

public export method test() -> void:
    int x = 1
    {int=>int} map = {1=>x, 3=>2}
    assert map == {1=>1,3=>2}
