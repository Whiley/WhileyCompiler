

public export method test() -> void:
    {int} xs = {1, 2, 3, 4}
    {int} ys = xs + {5, 1}
    assume xs == {1,2,3,4}
    xs = xs + {6}
    assume xs == {1,2,3,4,6}
    assume ys == {1,2,3,4,5}
