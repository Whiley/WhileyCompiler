

public export method test() -> void:
    {int} xs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
    assume (xs & {2, 3, 7, 11}) == {2,3,7}
