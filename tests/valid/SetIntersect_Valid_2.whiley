

public export method test() -> void:
    {int} xs = {1, 2, 3}
    [int] ys = [2, 3, 4]
    {int} zs = xs & ({int}) ys
    assume zs == {2,3}
