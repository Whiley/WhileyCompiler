

public export method test() -> void:
    {int=>int} l = {1=>2, 2=>3}
    assume l == {1=>2,2=>3}
    assume |l| == 2
    l[3] = 123
    assume l == {1=>2,2=>3,3=>123}
    assume |l| == 3
