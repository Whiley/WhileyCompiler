

public export method test() -> void:
    {int} x = {1, 2, 3}
    {int} y = {1, 2}
    assume x - y == {3}
    x = {'a', 'b', 'c'}
    y = {'b', 'c'}
    assume x - y == {'a'}
