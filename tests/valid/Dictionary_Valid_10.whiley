import whiley.lang.*

public method main(System.Console sys) -> void:
    {int=>int} l = {1=>2, 2=>3}
    assume l == {1=>2,2=>3}
    assume |l| == 2
    l[3] = 123
    assume l == {1=>2,2=>3,3=>123}
    assume |l| == 3
