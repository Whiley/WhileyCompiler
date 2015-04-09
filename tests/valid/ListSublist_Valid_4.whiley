import whiley.lang.*

method main(System.Console sys) -> void:
    [int] list = [1, 2, 3]
    [int] sublist = list[0..2]
    assert list == [1,2,3]
    assert sublist == [1,2]
